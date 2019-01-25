package com.luckyaf.kommon.extension

import java.io.File
import java.net.URLConnection

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */

fun File.mediaType(): String {
    return URLConnection.getFileNameMap().getContentTypeFor(name) ?: when (extension.toLowerCase()) {
        "json" -> "application/json"
        "js" -> "application/javascript"
        "apk" -> "application/vnd.android.package-archive"
        "md" -> "text/x-markdown"
        "webp" -> "image/webp"
        else -> "application/octet-stream"
    }
}