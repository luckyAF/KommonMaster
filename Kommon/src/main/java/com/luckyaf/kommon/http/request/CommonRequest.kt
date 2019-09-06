package com.luckyaf.kommon.http.request

import android.os.Handler
import android.os.Looper
import com.alibaba.fastjson.JSONObject
import com.google.gson.JsonObject
import com.luckyaf.kommon.extension.mediaType
import com.luckyaf.kommon.extension.toJavaBean
import com.luckyaf.kommon.http.SmartHttp
import com.luckyaf.kommon.http.callback.CommonCallback
import com.luckyaf.kommon.http.callback.HttpCallback
import com.luckyaf.kommon.http.internal.*
import io.reactivex.Observable
import kotlinx.coroutines.CompletableDeferred
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeoutException

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
data class CommonRequest(
        @HttpMethod
        var method: String = HttpMethod.GET,
        private var mTag: Any? = null,
        private var mUrl: String = "",
        private var mHeaders: LinkedList<Pair<String, String>> = LinkedList(),
        private var mParams: LinkedList<Pair<String, Any>> = LinkedList()
) {
    companion object {
        fun get() = CommonRequest(HttpMethod.GET)
        fun post() = CommonRequest(HttpMethod.POST)
        fun head() = CommonRequest(HttpMethod.HEAD)
        fun patch() = CommonRequest(HttpMethod.PATCH)
        fun put() = CommonRequest(HttpMethod.PUT)
        fun delete() = CommonRequest(HttpMethod.DELETE)
    }


    /**
     * 在原请求线程返回数据
     * 默认返回到主线程
     */
    private var backOnOldThread = false

    /**
     * 主线程的handler
     */
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var usingJson = false
    var errorMessage = ""

    fun tag(tag: Any): CommonRequest {
        mTag = tag
        return this
    }

    fun url(url: String): CommonRequest {
        mUrl = url
        // 如果 tag为空 则设置tag为当前url
        mTag = mTag ?: mUrl
        return this
    }

    fun useJson(use: Boolean): CommonRequest {
        usingJson = use
        return this
    }

    fun headers(vararg headers: Pair<String, Any>): CommonRequest {
        headers.forEach { this.mHeaders.add(Pair(it.first, "${it.second}")) }
        return this
    }

    fun headers(map: Map<String, Any>): CommonRequest {
        map.forEach { this.mHeaders.add(Pair(it.key, "${it.value}")) }
        return this
    }

    fun header(key: String, value: Any): CommonRequest {
        this.mHeaders.add(Pair(key, "$value"))
        return this
    }

    fun params(vararg params: Pair<String, Any>): CommonRequest {
        params.forEach {
            this.mParams.add(Pair(it.first, it.second))
        }
        return this
    }


    fun params(map: Map<String, Any>): CommonRequest {
        map.forEach {
            this.mParams.add(Pair(it.key, it.value))
        }
        return this
    }

    fun param(key: String, value: Any): CommonRequest {
        this.mParams.add(Pair(key, value))
        return this
    }

    fun backOnOld(onOld: Boolean): CommonRequest {
        this.backOnOldThread = onOld
        return this
    }

    fun getUrl() = mUrl
    fun tag() = mTag
    private fun params() = mParams


    inline fun <reified T> request(func: CommonCallback<T>.() -> Unit) {
        val real = CommonCallback<T>()
        real.func()
        request(real)
    }

    /**
     * 普通的 callback回调
     */
    inline fun <reified T> request(callback: HttpCallback<T>) {
        val req = buildRequest()
        if (null == req) {
            callback.onError(Exception(errorMessage))
            return
        }
        SmartHttp.mOkHttpClient.newCall(req).apply {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    commonCallback {
                        when (e) {
                            //is SocketException -> callback.onNetError()
                            is SocketTimeoutException -> callback.onError(Exception("连接超时"))
                            is TimeoutException -> callback.onError(Exception("连接超时"))
                            else -> callback.onError(e)
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val result = response.body!!.string()
                        try {

                            val resultCallback =
                                    if (T::class.java == String::class.java) {
                                        result as T
                                    } else {
                                        SmartHttp.convert(result)
                                    }
                            commonCallback {
                                callback.onSuccess(resultCallback)
                            }
                        } catch (e: Exception) {
                            commonCallback {
                                callback.onError(Exception("数据解析异常"))
                            }
                        }
                    } else {
                        commonCallback {
                            callback.onError(Exception("request to ${getUrl()} is fail; http code: ${response.code}!"))
                        }
                    }
                }
            })
        }
    }

    /**
     * 协程
     */
    suspend inline fun <reified T> suspendRequest(): T? {
        val req = buildRequest()
        val deferred = CompletableDeferred<T?>()

        if (null == req) {
            deferred.completeExceptionally(IOException(errorMessage))
            return deferred.await()
        }
        val call = SmartHttp.mOkHttpClient.newCall(req)
        deferred.invokeOnCompletion {
            if (deferred.isCancelled) {
                call.cancel()
            }
        }
        try {
            val response = call.execute()
            if (response.isSuccessful && response.body != null) {
                when {
                    T::class.java == String::class.java -> deferred.complete(response.body!!.string() as T)
                    else ->
                        deferred.complete(
                                SmartHttp.convert(response.body!!.string())
                        )
                }
            } else {
                deferred.completeExceptionally(Exception("数据异常"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            deferred.completeExceptionally(e)
        }
        return deferred.await()
    }


    /**
     * rxjava
     */
    inline fun <reified T> asObservable(): Observable<T> {
        val parser = object : Parser<T> {
            override fun onParse(response: Response): T {
                //return SmartHttp.convert(response.body()!!.string())
                return response.body!!.string().toJavaBean<T>()
            }
        }
        return ObservableHttp(
                SmartHttp.mOkHttpClient,
                buildRequest(),
                parser
        )
    }


    fun buildRequest(): Request? {
        return try {
            when (method) {
                HttpMethod.GET -> buildGetRequest()
                HttpMethod.HEAD -> buildHeadRequest()
                HttpMethod.POST -> buildPostRequest()
                HttpMethod.DELETE -> buildDeleteRequest()
                HttpMethod.PUT -> buildPutRequest()
                HttpMethod.PATCH -> buildPatchRequest()
                else -> buildPostRequest()
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Request build onError"
            null
        }
    }

    private fun buildGetRequest(): Request {
        return Request.Builder().url(urlParams())
                .apply {
                    SmartHttp.globalHeaders.forEach {
                        addHeader(it.key, it.value)
                    }
                    mHeaders.forEach {
                        addHeader(it.first, it.second)
                    }
                }
                .get()
                .tag(mTag!!)
                .build()
    }

    private fun buildHeadRequest(): Request {
        return Request.Builder().url(urlParams())
                .apply {
                    SmartHttp.globalHeaders.forEach { addHeader(it.key, it.value) }
                    mHeaders.forEach { addHeader(it.first, it.second) }
                }
                .head().tag(mTag!!).build()
    }

    private fun buildPostRequest(): Request {
        return bodyBuilder().post(buildRequestBody()).tag(mTag!!).build()
    }

    private fun buildPutRequest(): Request {
        return bodyBuilder().put(buildRequestBody()).tag(mTag!!).build()
    }

    private fun buildDeleteRequest(): Request {
        return bodyBuilder().delete(buildRequestBody()).tag(mTag!!).build()
    }

    private fun buildPatchRequest(): Request {
        return bodyBuilder().patch(buildRequestBody()).tag(mTag!!).build()
    }

    private fun urlParams(): String {
        val queryParams =
                if (params().isEmpty()) ""
                else "?" + mParams.joinToString(separator = "&", transform = {
                    "${it.first}=${it.second}"
                })

        return "$mUrl$queryParams"
    }

    private fun bodyBuilder(): Request.Builder {
        return Request.Builder().url(mUrl)
                .apply {
                    SmartHttp.globalHeaders.forEach { addHeader(it.key, it.value) }
                    mHeaders.forEach { addHeader(it.first, it.second) }
                }
    }

    private fun buildRequestBody(): RequestBody {
        return when {

            isMultiPart() -> {
                // multipart/form-data
                val builder = MultipartBody.Builder()
                mParams.forEach {
                    if (it.second is String) {
                        builder.addFormDataPart(it.first, it.second as String)
                    } else if (it.second is File) {
                        val file = it.second as File
                        builder.addFormDataPart(
                                it.first,
                                file.name,
                                file.asRequestBody(file.mediaType().toMediaTypeOrNull())
                        )
                    }
                }
                builder.setType(MultipartBody.FORM).build()
            }
            usingJson -> {
                getJsonString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            }
            else -> {
                // form-data url-encoded
                val builder = FormBody.Builder()
                mParams.forEach { builder.add(it.first, it.second.toString()) }
                builder.build()
            }
        }

    }

    private fun getJsonString(): String {
        val jsonObject = JSONObject()

        mParams.forEach {

            jsonObject[it.first] = it.second
        }
        return jsonObject.toJSONString()
    }

    private fun isMultiPart() = mParams.any { it.second is File }

    fun commonCallback(f: () -> Unit) {
        if (backOnOldThread) {
            f.invoke()
        } else {
            mainThreadHandler.post(f)
        }
    }


}