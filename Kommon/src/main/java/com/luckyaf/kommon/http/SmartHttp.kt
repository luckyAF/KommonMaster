package com.luckyaf.kommon.http

import android.util.ArrayMap
import com.google.gson.reflect.TypeToken
import com.luckyaf.kommon.http.interceptor.HttpLogInterceptor
import com.luckyaf.kommon.http.request.CommonRequest
import com.luckyaf.kommon.http.request.DownloadRequest
import com.luckyaf.kommon.http.request.UploadRequest
import com.luckyaf.kommon.utils.GsonUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-19
 *
 */
object SmartHttp {

    var httpTimeout = 10000L  //超时时间 10s
    var globalHeaders = ArrayMap<String, String>()// 通用 header
    var mGson = GsonUtil.provideGson()
    var mOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLogInterceptor())
            .readTimeout(httpTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(httpTimeout, TimeUnit.MILLISECONDS)
            .connectTimeout(httpTimeout, TimeUnit.MILLISECONDS)
            .build()

    fun provideNewDownloadClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(HttpLogInterceptor())
                .readTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .build()
    }


    /**
     * 添加全局公共Header
     */
    fun addHeader(key: String, value: String) {
        globalHeaders[key] = value
    }


    /**
     * 设置拦截器
     */
    fun interceptors(vararg interceptors: Interceptor) {
        val builder = mOkHttpClient.newBuilder()
        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }
        mOkHttpClient = builder.build()

    }


    /**
     * 设置自定义的Client
     */
    fun setClient(client: OkHttpClient) {
        mOkHttpClient = client
    }

    fun cancelTag(tag: Any?) {
        tag?.let {
            val queuedCalls = mOkHttpClient.dispatcher().queuedCalls()
            for (call in queuedCalls) {
                if (tag === call.request().tag()) {
                    call.cancel()
                }
            }
            val runningCalls = mOkHttpClient.dispatcher().runningCalls()
            for (call in runningCalls) {
                if (tag === call.request().tag()) {
                    call.cancel()
                }
            }
        }
    }


    fun cancelAll() {
        val queuedCalls = mOkHttpClient.dispatcher().queuedCalls()
        for (call in queuedCalls) {
            call.cancel()
        }
        val runningCalls = mOkHttpClient.dispatcher().runningCalls()
        for (call in runningCalls) {
            call.cancel()
        }
    }


    fun get() = CommonRequest.get()

    fun post() = CommonRequest.post()

    fun head() = CommonRequest.head()

    fun patch() = CommonRequest.patch()

    fun put() = CommonRequest.put()

    fun delete() = CommonRequest.delete()

    fun download(url: String) = DownloadRequest(url = url)

    fun upload() = UploadRequest()


     inline fun <reified T> convert(jsonString: String): T {
        return mGson.fromJson(jsonString, object : TypeToken<T>() {}.type)
    }

}