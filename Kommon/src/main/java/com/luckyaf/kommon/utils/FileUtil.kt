package com.luckyaf.kommon.utils

import android.net.Uri
import android.text.TextUtils
import com.luckyaf.kommon.extension.safeSubstring
import okhttp3.Response
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLConnection
import java.net.URLDecoder

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
object FileUtil {

    private val TEMP_DIR_PATH = ""

    /** 根据响应头或者url获取文件名  */
    fun getNetFileName(response: Response, url: String): String {
        var fileName = getHeaderFileName(response)
        if (TextUtils.isEmpty(fileName)) {
            fileName = getUrlFileName(url)
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = "unknownFile_" + System.currentTimeMillis()
        }
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return fileName?:"unknownFile"
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    fun getHeaderFileName(response: Response): String? {
        var dispositionHeader = response.header("Content-Disposition")
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replace("\"".toRegex(), "")
            var split = "filename="
            var indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                return dispositionHeader.safeSubstring(
                        indexOf + split.length,
                        dispositionHeader.length
                )
            }
            split = "filename*="
            indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                var fileName = dispositionHeader.safeSubstring(
                        indexOf + split.length,
                        dispositionHeader.length
                )
                val encode = "UTF-8''"
                if (fileName.startsWith(encode)) {
                    fileName = fileName.safeSubstring(encode.length, fileName.length)
                }
                return fileName
            }
        }
        return null
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    fun getUrlFileName(url: String): String? {
        var filename: String? = null
        val strings = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (string in strings) {
            if (string.contains("?")) {
                val endIndex = string.indexOf("?")
                if (endIndex != -1) {
                    filename = string.safeSubstring(0, endIndex)
                    return filename
                }
            }
        }
        if (strings.size > 0) {
            filename = strings[strings.size - 1]
        }
        return filename
    }

    fun getFileType(file: File): String {
        var fileName = file.name
        var typename: String? = URLConnection.getFileNameMap().getContentTypeFor(fileName)
        if (null == typename) {
            fileName = fileName.safeSubstring( fileName.lastIndexOf(".") + 1)
            when (fileName) {
                "json" -> typename = "application/json"
                "js" -> typename = "application/javascript"
                "apk" -> typename = "application/vnd.android.package-archive"

                "md" -> typename = "text/x-markdown"

                "webp" -> typename = "image/webp"

                else -> typename = "application/octet-stream"
            }
        }
        return typename

    }


    /**
     * Delete file or folder.
     *
     * @param path path.
     * @return is succeed.
     * @see .delFileOrFolder
     */
    fun delFileOrFolder(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) false else delFileOrFolder(File(path))
    }

    /**
     * Delete file or folder.
     *
     * @param path path.
     * @return is succeed.
     * @see .delFileOrFolder
     */
    fun delFileOrFolder(path: String, name: String): Boolean {
        return if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) false else delFileOrFolder(
                File(
                        path,
                        name
                )
        )
    }

    /**
     * Delete file or folder.
     *
     * @param file file.
     * @return is succeed.
     * @see .delFileOrFolder
     */
    fun delFileOrFolder(file: File?): Boolean {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile) {
            file.delete()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (sonFile in files) {
                    delFileOrFolder(sonFile)
                }
            }
            file.delete()
        }
        return true
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String): File? {
        return if (TextUtils.isEmpty(filePath)) null else File(filePath)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}