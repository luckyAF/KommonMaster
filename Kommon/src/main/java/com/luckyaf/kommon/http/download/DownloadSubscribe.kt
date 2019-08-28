package com.luckyaf.kommon.http.download

import com.luckyaf.kommon.extension.no
import com.luckyaf.kommon.http.callback.ReadCallback
import com.luckyaf.kommon.http.constants.HeaderConstant
import com.luckyaf.kommon.http.constants.StatusConstant
import com.luckyaf.kommon.http.internal.Progress
import com.luckyaf.kommon.http.request.DownloadRequest
import com.luckyaf.kommon.utils.IOUtil
import com.luckyaf.kommon.utils.RxUtil
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.io.BufferedInputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
class DownloadSubscribe(private val downloadRequest: DownloadRequest) : ObservableOnSubscribe<Progress> {

    var speedLimit: Int = 0
    var notifyInterval: Int = 0
    private var disposable: Disposable? = null
    private var emitter: ObservableEmitter<Progress>? = null
    private var currentSize: Long = 0
    private var tempReadSize: Long = 0
    private val speedBuffer = ArrayList<Long>(4)

    init {
        speedLimit = Int.MAX_VALUE
        notifyInterval = 300
        tempReadSize = 0
        currentSize = downloadRequest.currentSize
    }

    fun setSpeedLimit(speed: Int): DownloadSubscribe {
        speedLimit = speed
        return this
    }

    fun setNotifyInterval(interval: Int): DownloadSubscribe {
        notifyInterval = interval
        return this
    }


    override fun subscribe(emitter: ObservableEmitter<Progress>) {
        this.emitter = emitter
        disposable = RxUtil.loopDoing(notifyInterval, TimeUnit.MILLISECONDS) {
            sendProgress()
        }
        try {
            if (StatusConstant.FINISHED == downloadRequest.status) {
                throw Exception("下载任务已完成")
            } else if (StatusConstant.ERROR == downloadRequest.status) {
                throw Exception("下载任务异常")
            }

            // ready go
            downloadRequest.status = StatusConstant.LOADING
            updateRequest(downloadRequest)
            if (downloadRequest.supportRange) {
                //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                downloadRequest.header(
                    HeaderConstant.HEAD_KEY_RANGE,
                    "bytes=" + downloadRequest.currentSize + "-" + downloadRequest.totalSize
                )
                if (downloadRequest.lastModify.isNotBlank()) {
                    downloadRequest.header(
                        HeaderConstant.HEAD_KEY_IF_RANGE,
                        downloadRequest.lastModify
                    )
                }
            }
            val response = downloadRequest.call().execute()

            if (downloadRequest.currentSize > 0) {
                if (response.code != 206) {
                    downloadRequest.status = StatusConstant.ERROR
                    throw Exception("远端资源已更改，请重新下载")
                }
            }
            val body = response.body
            val responseBody =
                DownloadResponseBody.upgrade(body!!, speedLimit, object : ReadCallback {
                    override fun call(size: Int) {
                        tempReadSize += size.toLong()
                    }
                })
            val filePath = File(downloadRequest.fileDir)
            try {
                if (!filePath.exists()) {
                    filePath.mkdirs()
                }
            } catch (e: Exception) {
                throw e
            }

            val file = File(downloadRequest.fileDir, downloadRequest.fileName)

            if (file.length() < downloadRequest.currentSize) {
                throw Exception("本地资源已更改，请重新下载")
            }

            //start downloading
            val randomAccessFile: RandomAccessFile
            try {
                randomAccessFile = RandomAccessFile(file, "rw")
                randomAccessFile.seek(downloadRequest.currentSize)
            } catch (e: Exception) {
                throw e
            }


            val responseStream = responseBody.byteStream()
            val inputStream = BufferedInputStream(responseStream, 8 * 1024)
            val buffer = ByteArray(1024 * 8)//缓冲数组8kB
            try {
                var len = inputStream.read(buffer)
                while (len != -1) {
                    randomAccessFile.write(buffer, 0, len)
                    currentSize += len.toLong()
                    downloadRequest.currentSize = currentSize
                    len = inputStream.read(buffer)
                }
                inputStream.close()
                downloadRequest.status = StatusConstant.FINISHED
                sendProgress()
            } catch (e: Exception) {
                //emitter.onError(e);
            } finally {
                //关闭IO流
                IOUtil.close(randomAccessFile,inputStream,responseStream)
            }
            emitter.onComplete()//完成
            disposable?.isDisposed?.no {
                disposable?.dispose()
            }

        } catch (e: Exception) {
            onError(e)
        }
    }


    private fun sendProgress() {
        var speed = tempReadSize * 1000 / notifyInterval
        speed = bufferSpeed(speed)
        tempReadSize = 0
        val fraction = downloadRequest.currentSize / (downloadRequest.totalSize * 1.0f)
        onProgress(
            Progress(speed, downloadRequest.currentSize, downloadRequest.totalSize, fraction)
        )
    }

    private fun onProgress(progress: Progress) {
        updateRequest(downloadRequest)
        this.emitter?.onNext(progress)
    }

    private fun onError(throwable: Throwable) {
        this.emitter?.onError(throwable)
        disposable?.isDisposed?.no {
            disposable?.dispose()
        }
    }


    /** 平滑网速，避免抖动过大  */
    private fun bufferSpeed(speed: Long): Long {
        speedBuffer.add(speed)
        if (speedBuffer.size > 3) {
            speedBuffer.removeAt(0)
        }
        var sum: Long = 0
        for (speedTemp in speedBuffer) {
            sum += speedTemp
        }
        return sum / speedBuffer.size
    }

    private fun updateRequest(request: DownloadRequest) {
        DownloadManager.updateDownloadRequest(request)
    }

}