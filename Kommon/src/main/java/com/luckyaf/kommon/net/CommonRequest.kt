package com.luckyaf.kommon.net

import com.luckyaf.kommon.extension.mediaType
import okhttp3.*
import java.io.File

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
@Suppress("unused")
data class CommonRequest(
        private var tag: Any = CommonRequest::class.java,
        @HttpMethod var method: String = HttpMethod.GET,
        private var url: String = "",
        private var headers: ArrayList<Pair<String, String>> = arrayListOf(),
        private var params: ArrayList<Pair<String, Any>> = arrayListOf()
) {
    fun headers(vararg headers: Pair<String, Any>): CommonRequest {
        headers.forEach { this.headers.add(Pair(it.first, "${it.second}")) }
        return this
    }

    fun headers(map: Map<String, Any>): CommonRequest {
        map.forEach { this.headers.add(Pair(it.key, "${it.value}")) }
        return this
    }

    fun params(vararg params: Pair<String, Any>): CommonRequest {
        params.forEach { this.params.add(Pair(it.first, if (it.second is File) it.second else "${it.second}")) }
        return this
    }

    fun params(map: Map<String, Any>): CommonRequest {
        map.forEach { this.params.add(Pair(it.key, if (it.value is File) it.value else "${it.value}")) }
        return this
    }


    fun url() = url
    fun tag() = tag
    private fun params() = params




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
                    NetManager.globalHeaders.forEach { addHeader(it.first, it.second) }
                    headers.forEach { addHeader(it.first, it.second) }
                }
                .get()
                .tag(tag)
                .build()
    }

    private fun buildHeadRequest(): Request {
        return Request.Builder().url(urlParams())
                .apply {
                    NetManager.globalHeaders.forEach { addHeader(it.first, it.second) }
                    headers.forEach { addHeader(it.first, it.second) }
                }
                .head().tag(tag).build()
    }

    private fun buildPostRequest(): Request {
        return bodyBuilder().post(buildRequestBody()).tag(tag).build()
    }

    private fun buildPutRequest(): Request {
        return bodyBuilder().put(buildRequestBody()).tag(tag).build()
    }

    private fun buildDeleteRequest(): Request {
        return bodyBuilder().delete(buildRequestBody()).tag(tag).build()
    }

    private fun buildPatchRequest(): Request {
        return bodyBuilder().patch(buildRequestBody()).tag(tag).build()
    }


    private fun bodyBuilder(): Request.Builder {
        return Request.Builder().url(url())
                .apply {
                    NetManager.globalHeaders.forEach { addHeader(it.first, it.second) }
                    headers.forEach { addHeader(it.first, it.second) }
                }
    }


    private fun buildRequestBody(): RequestBody {
        return if (isMultiPart()) {
            // multipart/form-data
            val builder = MultipartBody.Builder()
            params.forEach {
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
            params.forEach { builder.add(it.first, it.second as String) }
            builder.build()
        }
    }


    private fun isMultiPart() = params.any { it.second is File }

    private fun urlParams(): String {
        val queryParams =
                if (params().isEmpty()) ""
                else "?" + params.joinToString(separator = "&", transform = {
                    "${it.first}=${it.second}"
                })

        return "${url()}$queryParams"
    }


}