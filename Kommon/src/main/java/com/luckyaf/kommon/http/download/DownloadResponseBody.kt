package com.luckyaf.kommon.http.download

import com.luckyaf.kommon.http.callback.ReadCallback
import com.luckyaf.kommon.http.internal.LimitInputStream
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Okio
import okio.buffer
import okio.source

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
class DownloadResponseBody private constructor(
    private val responseBody: ResponseBody,
    private val limitSpeed: Int,
    private val readListener: ReadCallback?
) : ResponseBody() {
    private var progressSource: BufferedSource? = null

    companion object {
        fun upgrade(
            responseBody: ResponseBody,
            limitSpeed: Int,
            readListener: ReadCallback?
        ): ResponseBody {
            return DownloadResponseBody(responseBody, limitSpeed, readListener)
        }
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        readListener ?: return responseBody.source()
        val inputStream =
            LimitInputStream(responseBody.source().inputStream(), limitSpeed, readListener)
        progressSource = inputStream.source().buffer()

        return progressSource!!
    }

    override fun close() {
        try {
            progressSource?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}