//package com.luckyaf.kommon.http.request
//
//import com.luckyaf.kommon.extension.mediaType
//import com.luckyaf.kommon.http.SmartHttp
//import com.luckyaf.kommon.http.callback.HttpCallback
//import com.luckyaf.kommon.http.internal.HttpMethod
//import okhttp3.*
//import java.io.File
//import java.io.IOException
//import java.net.SocketTimeoutException
//import java.util.*
//import java.util.concurrent.TimeoutException
//
///**
// * 类描述：
// * @author Created by luckyAF on 2019-08-05
// *
// */
//class UploadRequest(
//        @HttpMethod
//        var method: String = HttpMethod.POST,
//        private var mTag: Any? = null,
//        private var mUrl: String = "",
//        private var mHeaders: LinkedList<Pair<String, String>> = LinkedList(),
//        private var mParams: LinkedList<Pair<String, Any>> = LinkedList()
//) {
//
//
//    fun headers(vararg headers: Pair<String, Any>): UploadRequest {
//        headers.forEach { this.mHeaders.add(Pair(it.first, "${it.second}")) }
//        return this
//    }
//
//    fun headers(map: Map<String, Any>): UploadRequest {
//        map.forEach { this.mHeaders.add(Pair(it.key, "${it.value}")) }
//        return this
//    }
//
//    fun header(key: String, value: Any): UploadRequest {
//        this.mHeaders.add(Pair(key, "$value"))
//        return this
//    }
//
//    fun params(vararg params: Pair<String, Any>): UploadRequest {
//        params.forEach {
//            this.mParams.add(Pair(it.first, it.second))
//        }
//        return this
//    }
//
//    fun params(map: Map<String, Any>): UploadRequest {
//        map.forEach {
//            this.mParams.add(Pair(it.key, it.value))
//        }
//        return this
//    }
//
//    fun param(key: String, value: Any): UploadRequest {
//        this.mParams.add(Pair(key, value))
//        return this
//    }
//
//
//    /**
//     * 普通的 callback回调
//     */
//    inline fun <reified T> request(callback: HttpCallback<T>) {
//        val req = buildRequest()
//        if (null == req) {
//            callback.onError(Exception(errorMessage))
//            return
//        }
//        SmartHttp.mOkHttpClient.newCall(req).apply {
//            enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    commonCallback {
//                        when (e) {
//                            //is SocketException -> callback.onNetError()
//                            is SocketTimeoutException -> callback.onError(Exception("连接超时"))
//                            is TimeoutException -> callback.onError(Exception("连接超时"))
//                            else -> callback.onError(e)
//                        }
//                    }
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        val result = response.body()!!.string()
//                        try {
//
//                            val resultCallback =
//                                    if (T::class.java == String::class.java) {
//                                        result as T
//                                    } else {
//                                        SmartHttp.convert(result)
//                                    }
//                            commonCallback {
//                                callback.onSuccess(resultCallback)
//                            }
//                        } catch (e: Exception) {
//                            commonCallback {
//                                callback.onError(Exception("数据解析异常"))
//                            }
//                        }
//                    } else {
//                        commonCallback {
//                            callback.onError(Exception("request to ${getUrl()} is fail; http code: ${response.code()}!"))
//                        }
//                    }
//                }
//            })
//        }
//    }
//
//    private fun bodyBuilder(): Request.Builder {
//        return Request.Builder().url(mUrl)
//                .apply {
//                    SmartHttp.globalHeaders.forEach { addHeader(it.key, it.value) }
//                    mHeaders.forEach { addHeader(it.first, it.second) }
//                }
//    }
//     fun buildRequest(): Request {
//        return bodyBuilder().post(buildRequestBody()).tag(mTag!!).build()
//    }
//
//    fun buildRequestBody(): RequestBody {
//        val builder = MultipartBody.Builder()
//        mParams.forEach {
//            if (it.second is File) {
//                val file = it.second as File
//                builder.addFormDataPart(
//                        it.first,
//                        file.name,
//                        RequestBody.create(MediaType.parse(file.mediaType()), file)
//                )
//            } else {
//                builder.addFormDataPart(it.first, it.second.toString())
//
//            }
//        }
//        return builder.setType(MultipartBody.FORM).build()
//
//    }
//    fun commonCallback(f: () -> Unit) {
//        if (backOnOldThread) {
//            f.invoke()
//        } else {
//            mainThreadHandler.post(f)
//        }
//    }
//
//}
//
//
//
