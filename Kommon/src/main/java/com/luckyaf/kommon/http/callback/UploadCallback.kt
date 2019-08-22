package com.luckyaf.kommon.http.callback

import com.luckyaf.kommon.http.internal.Progress
import okhttp3.ResponseBody
import java.io.File
import java.lang.Exception

/**
 * 类描述：上传回调
 * @author Created by luckyAF on 2019-08-05
 *
 */
interface UploadCallback {
    fun onStart()
    fun onError(e: Exception)
    fun onProgress(progress: Progress)
    fun onComplete()
    fun onFinish(responseBody: ResponseBody)
}