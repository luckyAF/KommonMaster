package com.luckyaf.kommon.http.download

import com.luckyaf.kommon.extension.safeSubstring
import com.luckyaf.kommon.http.constants.HeaderConstant
import com.luckyaf.kommon.http.request.DownloadRequest
import com.luckyaf.kommon.utils.FileUtil
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import java.io.File
import java.io.IOException

/**
 * 类描述： 请求初始化
 * @author Created by luckyAF on 2019-08-06
 *
 */
class RequestInitSubscribe(private var downloadRequest:DownloadRequest) : ObservableOnSubscribe<DownloadRequest> {

    override fun subscribe(emitter: ObservableEmitter<DownloadRequest>) {
        val downloadCall = downloadRequest.call()
        try {
            val response = downloadCall.execute()
            if (response.isSuccessful) {
                val urlFileName = FileUtil.getNetFileName(response, downloadRequest.url)
                var totalSize: Long = -1
                if (null != response.body) {
                    val body = response.body
                    totalSize = body!!.contentLength()
                }
                if (totalSize == 0L) {
                    emitter.onError(Exception("error file"))
                }
                if (null == response.headers.get(HeaderConstant.HEAD_KEY_CONTENT_RANGE)
                    && null == response.headers.get(HeaderConstant.HEAD_KEY_ACCEPT_RANGES)
                ) {
                    downloadRequest.setSupportRange(false)
                } else {
                    if (null != response.headers.get(HeaderConstant.HEAD_KEY_LAST_MODIFIED)) {
                        downloadRequest.lastModify = response.headers.get(HeaderConstant.HEAD_KEY_LAST_MODIFIED) ?:""
                    }
                    if (null != response.headers.get(HeaderConstant.HEAD_KEY_E_TAG)) {
                        downloadRequest.lastModify = response.headers.get(HeaderConstant.HEAD_KEY_E_TAG)?:""
                    }
                }
                downloadRequest.setTotalSize(totalSize)
                response.close()
                updateFileName(urlFileName)
                emitter.onNext(downloadRequest)
                emitter.onComplete()
            } else {
                emitter.onError(Exception("can not get information"))
            }
        } catch (e: IOException) {
            emitter.onError(e)
            e.printStackTrace()
        }

    }
    private fun updateFileName(urlFileName:String) {
        var fileName = downloadRequest.fileName
        var downloadLength: Long = 0
        val totalSize = downloadRequest.totalSize


        if(fileName.isBlank()){
            fileName = urlFileName
            downloadRequest.setFileName(fileName)
        }
        var file = File(downloadRequest.fileDir, fileName)

        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length()
        }
        //之前下载过,需要重新来一个文件
        var i = 1
        while (downloadLength >= totalSize) {
            val dotIndex = fileName.lastIndexOf(".")
            val fileNameOther: String
            fileNameOther = if (dotIndex == -1) {
                "$fileName($i)"
            } else
                "${fileName.safeSubstring(0, dotIndex)}$i${fileName.safeSubstring(dotIndex)}"

            val newFile = File(downloadRequest.fileDir, fileNameOther)
            file = newFile
            downloadLength = newFile.length()
            i++
        }
        downloadRequest.setFileName(file.name)
        downloadRequest.updateTag()

    }

}