package com.luckyaf.kommon.net

import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
@Suppress("unused")
object NetManager {
    private var httpTimeout = 10000L  //超时时间 10s
    val globalHeaders = arrayListOf<Pair<String, String>>()// 通用 header
    val requestCache = hashMapOf<Any, Call>() // 请求列表 方便取消
    var mOkHttpClient:OkHttpClient  =
        OkHttpClient.Builder()
                .readTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLogInterceptor())
                .build()




    /**
     * 设置全局公共Header
     */
    fun headers(vararg headers: Pair<String, String>): NetManager {
        globalHeaders.clear()
        headers.forEach { globalHeaders.add(it) }
        return this
    }

    /**
     * 添加全局公共Header
     */
    fun addHeader(header: Pair<String, String>): NetManager {
        globalHeaders.add(header)
        return this
    }


    /**
     * 设置拦截器
     */
    fun interceptors(vararg interceptors: Interceptor): NetManager {
        val builder = mOkHttpClient.newBuilder()
        interceptors.forEach { builder.addInterceptor(it) }
        mOkHttpClient = builder.build()
        return this
    }


    /**
     * 设置自定义的Client
     */
    fun setClient(client: OkHttpClient){
        mOkHttpClient = client
    }

    fun cancel(tag: Any){
        requestCache[tag]?.cancel()
        requestCache.remove(tag)
    }


    fun get(tag:Any,url:String) = CommonRequest(tag,HttpMethod.GET,url)
    fun post(tag:Any,url:String) = CommonRequest(tag,HttpMethod.POST,url)
    fun head(tag:Any,url:String) = CommonRequest(tag,HttpMethod.HEAD,url)
    fun patch(tag:Any,url:String) = CommonRequest(tag,HttpMethod.PATCH,url)
    fun put(tag:Any,url:String) = CommonRequest(tag,HttpMethod.PUT,url)
    fun delete(tag:Any,url:String) = CommonRequest(tag,HttpMethod.DELETE,url)








}