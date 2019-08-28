package com.luckyaf.kommon.http.interceptor

import com.luckyaf.kommon.BuildConfig
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.GzipSource
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-03
 *
 */
class HttpLogInterceptor @JvmOverloads constructor(
    private val printResponseHeader: Boolean = false,
    private val logger: Logger = Logger.providerLogger()) : Interceptor {
    private val requestPrefix = "--->"
    private val responsePrefix = "<---"
    interface Logger {
        fun log(message: String)
        companion object {
            /** A [Logger] defaults output appropriate for the current platform.  */
            private val DEBUG: Logger = object : Logger {
                override fun log(message: String) {
                    Platform.get().log(Platform.INFO, message, null)
                }
            }
            /** A [Logger] defaults output appropriate for the current platform.  */
            private val RELEASE: Logger = object : Logger {
                override fun log(message: String) {
                }
            }

            fun providerLogger():Logger{
                return if(BuildConfig.DEBUG){
                    DEBUG
                }else{
                    RELEASE
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body
        val connection = chain.connection()
        // 1. 请求第一行
        var requestMessage = "$requestPrefix ${request.method} ${request.url} ${connection?.protocol() ?: ""}\n"
        // 2. 请求头，只拼自定义的头
        requestMessage += header2String(request.headers)


        // 3. 请求体
        if (bodyHasUnknownEncoding(request.headers)) {
            requestMessage += "\n$requestPrefix END ${request.method} (encoded body omitted)"
        } else if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            var charset: Charset? = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            requestMessage += "\n"
            if (isPlaintext(buffer)) {
                requestMessage += buffer.readString(charset!!)
                requestMessage += "\n$requestPrefix END ${request.method}"
            } else {
                requestMessage += "\n$requestPrefix END ${request.method} (binary ${requestBody.contentLength()} -byte body omitted)"
            }
        } else {
            requestMessage += "\n$requestPrefix END ${request.method} (no request body)"
        }
        // 4. 打印请求信息
        logger.log(requestMessage)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("$responsePrefix HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) contentLength.toString() + "-byte" else "unknown-length"
        var responseMessage = "$responsePrefix ${response.code} ${if (response.message.isEmpty()) "" else response.message} "
        responseMessage += response.request.url
        responseMessage += " (" + tookMs + "ms" + ", $bodySize body)\n"

        // 是否拼接打印头
        val headers = response.headers
        if (printResponseHeader){
            responseMessage += header2String(headers)
        }

        if (!response.promisesBody()) {
            responseMessage += "\n$responsePrefix END HTTP"
        } else if (bodyHasUnknownEncoding(response.headers)) {
            responseMessage += "\n$responsePrefix END HTTP (encoded body omitted)"
        } else {
            val source = responseBody.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer()

            var gzippedLength: Long? = null
            if ("gzip".equals(headers.get("Content-Encoding"), ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            var charset: Charset? = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }

            responseMessage += "\n"
            if (!isPlaintext(buffer)) {
                responseMessage += "\n$responsePrefix END HTTP (binary " + buffer.size + "-byte body omitted)"
                return response
            }

            if (contentLength != 0L) {
                val responseData = buffer.clone().readString(charset!!)
                responseMessage += try {
                    JSONObject(responseData).toString(2)
                } catch (e: JSONException) {
                    // 不是json
                    responseData
                }
            }

            responseMessage += if (gzippedLength != null) {
                "\n$responsePrefix END HTTP (" + buffer.size + "-byte, $gzippedLength-gzipped-byte body)"
            } else {
                "\n$responsePrefix END HTTP (" + buffer.size + "-byte body)"
            }
        }
        logger.log(responseMessage)
        return response
    }

    private fun header2String(headers: Headers): String {
        var i = 0
        val count = headers.size
        var headerStr = ""
        while (i < count) {
            val name = headers.name(i)
            if (isExclude(name))
                headerStr += "\n    $name: ${headers.get(name)}"
            i++
        }
        return """headers = {$headerStr
}
        """.trimMargin()
    }




    private fun isExclude(name: String): Boolean {
        return listOf("X-Powered-By", "ETag", "Date", "Connection").all { !it.equals(name, true) }
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }

        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers.get("Content-Encoding")
            return (contentEncoding != null
                    && !contentEncoding.equals("identity", ignoreCase = true)
                    && !contentEncoding.equals("gzip", ignoreCase = true))
        }
    }
}
