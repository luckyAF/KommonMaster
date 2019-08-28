package com.luckyaf.kommon.http.interceptor

import com.luckyaf.kommon.http.callback.ReadCallback
import com.luckyaf.kommon.http.download.DownloadResponseBody
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
class DownloadInterceptor : Interceptor {

    private var callback: ReadCallback? = null
    private var speedLimit = -1

    constructor(limitSpeed: Int, readListener: ReadCallback) {
        this.callback = readListener
        this.speedLimit = limitSpeed
    }

    constructor(readListener: ReadCallback) {
        this.callback = readListener
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //拦截
        val originalResponse = chain.proceed(chain.request())
        //包装响应体并返回
        return if (null == originalResponse.body) {
            originalResponse
        } else {
            originalResponse.newBuilder()
                .body(DownloadResponseBody.upgrade(originalResponse.body!!, speedLimit, callback))
                .build()
        }

    }
}
