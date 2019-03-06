package com.luckyaf.kommon.net

import com.luckyaf.kommon.extension.mediaType
import com.luckyaf.kommon.extension.toJavaBean
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
@Suppress("unused")
data class CommonRequest(
        @HttpMethod var method: String = HttpMethod.GET,
        private var mTag: Any? = null,
        private var mUrl: String = "",
        private var mHeaders: ArrayList<Pair<String, String>> = arrayListOf(),
        private var mParams: ArrayList<Pair<String, Any>> = arrayListOf()
) {

    fun tag(tag: Any) {
        mTag = tag
    }

    fun url(url: String): CommonRequest {
        mUrl = url
        // 如果 tag为空 则设置tag为当前url
        mTag = mTag ?: mUrl
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
        params.forEach { this.mParams.add(Pair(it.first, if (it.second is File) it.second else "${it.second}")) }
        return this
    }


    fun params(map: Map<String, Any>): CommonRequest {
        map.forEach { this.mParams.add(Pair(it.key, if (it.value is File) it.value else "${it.value}")) }
        return this
    }

    fun param(key: String, value: Any): CommonRequest {
        this.mParams.add(Pair(key, "$value"))
        return this
    }

    fun getUrl() = mUrl
    fun tag() = mTag
    private fun params() = mParams


    fun buildRequest(): Request {
        return when (method) {
            HttpMethod.GET -> buildGetRequest()
            HttpMethod.HEAD -> buildHeadRequest()
            HttpMethod.POST -> buildPostRequest()
            HttpMethod.DELETE -> buildDeleteRequest()
            HttpMethod.PUT -> buildPutRequest()
            HttpMethod.PATCH -> buildPatchRequest()
            else -> buildPostRequest()
        }
    }


    private fun buildGetRequest(): Request {
        return Request.Builder().url(urlParams())
                .apply {
                    NetManager.globalHeaders.forEach {
                        addHeader(it.first, it.second)
                    }
                    mHeaders.forEach {
                        addHeader(it.first, it.second)
                    }
                }
                .get()
                .tag(mTag)
                .build()
    }

    private fun buildHeadRequest(): Request {
        return Request.Builder().url(urlParams())
                .apply {
                    NetManager.globalHeaders.forEach { addHeader(it.first, it.second) }
                    mHeaders.forEach { addHeader(it.first, it.second) }
                }
                .head().tag(mTag).build()
    }

    private fun buildPostRequest(): Request {
        return bodyBuilder().post(buildRequestBody()).tag(mTag).build()
    }

    private fun buildPutRequest(): Request {
        return bodyBuilder().put(buildRequestBody()).tag(mTag).build()
    }

    private fun buildDeleteRequest(): Request {
        return bodyBuilder().delete(buildRequestBody()).tag(mTag).build()
    }

    private fun buildPatchRequest(): Request {
        return bodyBuilder().patch(buildRequestBody()).tag(mTag).build()
    }


    private fun bodyBuilder(): Request.Builder {
        return Request.Builder().url(mUrl)
                .apply {
                    NetManager.globalHeaders.forEach { addHeader(it.first, it.second) }
                    mHeaders.forEach { addHeader(it.first, it.second) }
                }
    }


    private fun buildRequestBody(): RequestBody {
        return if (isMultiPart()) {
            // multipart/form-data
            val builder = MultipartBody.Builder()
            mParams.forEach {
                if (it.second is String) {
                    builder.addFormDataPart(it.first, it.second as String)
                } else if (it.second is File) {
                    val file = it.second as File
                    builder.addFormDataPart(it.first, file.name, RequestBody.create(MediaType.parse(file.mediaType()), file))
                }
            }
            builder.setType(MultipartBody.FORM).build()
        } else {
            // form-data url-encoded
            val builder = FormBody.Builder()
            mParams.forEach { builder.add(it.first, it.second as String) }
            builder.build()
        }
    }


    private fun isMultiPart() = mParams.any { it.second is File }

    private fun urlParams(): String {
        val queryParams =
                if (params().isEmpty()) ""
                else "?" + mParams.joinToString(separator = "&", transform = {
                    "${it.first}=${it.second}"
                })

        return "$mUrl$queryParams"
    }


    inline fun <reified T> request(): Deferred<T?> {
        return doRequest()
    }

    inline fun <reified T> request(func: CommonCallback<T>.() -> Unit) {
        val real = CommonCallback<T>()
        real.func()
        request(real)
    }


    inline fun <reified T> request(callback: CommonCallback<T>) {
        val req = buildRequest()
        NetManager.mOkHttpClient.newCall(req).apply {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                    if (e is SocketException) {
                        callback.onCancel()
                    }
                    if (e is SocketTimeoutException) {
                        callback.onTimeOut()
                    }
                    if (e is ConnectException) {
                        callback.onNetError()
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        if ("ktx" is T) {
                            callback.onSuccess(response.body()!!.string() as T)
                        } else {
                            callback.onSuccess(response.body()!!.string().toJavaBean<T>())
                        }
                    } else {
                        callback.onError(IOException("request to ${getUrl()} is fail; http code: ${response.code()}!"))
                    }
                }
            })
        }

    }


    inline fun <reified T> doRequest(): Deferred<T?> {
        val req = buildRequest()
        val call = NetManager.mOkHttpClient.newCall(req)
        val deferred = CompletableDeferred<T?>()
        deferred.invokeOnCompletion {
            if (deferred.isCancelled)
                call.cancel()
        }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//            deferred.completeExceptionally(e)
                deferred.complete(null) //pass null
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if ("String type" is T) {
                        deferred.complete(response.body()!!.string() as T)
                    } else {
                        deferred.complete(response.body()!!.string().toJavaBean<T>())
                    }
                } else {
                    //not throw
//              deferred.completeExceptionally(IOException(response))
                    onFailure(call, IOException("request to ${getUrl()} is fail; http code: ${response.code()}!"))
                }
            }
        })
        return deferred
    }


}