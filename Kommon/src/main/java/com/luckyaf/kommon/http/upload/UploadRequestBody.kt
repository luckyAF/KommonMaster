package com.luckyaf.kommon.http.upload

import com.luckyaf.kommon.extension.runOnMainScope
import com.luckyaf.kommon.http.callback.UploadCallback
import com.luckyaf.kommon.http.internal.BandWidthLimiter
import com.luckyaf.kommon.http.internal.Progress
import com.luckyaf.kommon.utils.RxUtil
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
class UploadRequestBody(
    private val requestBody: RequestBody,
    private val callback: UploadCallback?,
    private val limitSpeed: Int?
) : RequestBody() {

    private lateinit var countingSink: CountingSink
    private val mLoopSend: Disposable?
    private val notifyInterval = 300
    private var currentSize: Long = 0
    private var tempReadSize: Long = 0
    private var contentLength = 0L

    private val speedBuffer = ArrayList<Long>(4)
    private var bandWidthLimiter: BandWidthLimiter? = null

    init {
        currentSize = 0
        tempReadSize = 0
        limitSpeed?.let {
            bandWidthLimiter = BandWidthLimiter(it)
        }
        mLoopSend = RxUtil.loopDoing(notifyInterval, TimeUnit.MILLISECONDS) {
            sendProgress()
        }
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        try {
            return requestBody.contentLength()
        } catch (e: IOException) {
            clearDispose()
            runOnMainScope {
                callback?.onError(e)
            }
        }
        return -1
    }


    override fun writeTo(sink: BufferedSink) {
        try {
            countingSink = CountingSink(sink)
            //  SuperInputStream  inputStream = new SuperInputStream(responseBody.source().inputStream(),limitSpeed,readListener);
            val bufferedSink = Okio.buffer(countingSink)
            requestBody.writeTo(bufferedSink)
            bufferedSink.flush()
        } catch (e: IOException) {
            runOnMainScope {
                callback?.onError(e)
            }
        } finally {
            clearDispose()
            runOnMainScope {
                callback?.onComplete()
            }
        }
    }


    inner class CountingSink internal constructor(delegate: Sink) :
        ForwardingSink(delegate) {
        override fun write(source: Buffer, byteCount: Long) {
            try {
                bandWidthLimiter?.limitNextBytes(byteCount.toInt())
                super.write(source, byteCount)
            } catch (e: IOException) {
                e.printStackTrace()
                runOnMainScope {
                    clearDispose()
                    callback?.onError(e)
                }
            }
            if (contentLength == 0L) {
                contentLength = contentLength()
            }
            currentSize += byteCount
            tempReadSize += byteCount
            if (callback != null && currentSize == contentLength) {
                clearDispose()
            }
        }
    }

    private fun clearDispose() {
        if (null != mLoopSend && !mLoopSend.isDisposed) {
            mLoopSend.dispose()
        }
    }

    private fun sendProgress() {
        var speed = tempReadSize * 1000 / notifyInterval
        speed = bufferSpeed(speed)
        tempReadSize = 0
        val progress = currentSize / (contentLength * 1.0f)
        callback?.onProgress(Progress(speed, currentSize, contentLength, progress))
    }

    /**
     * 平滑网速，避免抖动过大
     */
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

}
