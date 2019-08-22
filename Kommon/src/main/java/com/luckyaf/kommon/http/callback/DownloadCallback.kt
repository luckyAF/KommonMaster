package com.luckyaf.kommon.http.callback

import com.luckyaf.kommon.http.internal.Progress
import java.io.File

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
interface DownloadCallback{
    fun onStart()
    fun onError(e: Exception)
    fun onProgress(progress: Progress)
    fun onFinish(data: File)
}