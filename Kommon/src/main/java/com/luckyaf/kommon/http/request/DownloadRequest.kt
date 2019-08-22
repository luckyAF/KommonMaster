package com.luckyaf.kommon.http.request

import android.text.TextUtils
import com.luckyaf.kommon.Kommon
import com.luckyaf.kommon.http.SmartHttp
import com.luckyaf.kommon.http.callback.DownloadCallback
import com.luckyaf.kommon.http.constants.StatusConstant
import com.luckyaf.kommon.http.download.DownloadManager
import com.luckyaf.kommon.http.download.RequestInitSubscribe
import com.luckyaf.kommon.http.internal.HttpMethod
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Call
import okhttp3.Request
import java.io.File
import java.util.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
data class DownloadRequest(
    @HttpMethod
    var method: String = HttpMethod.GET,
    var tag: String? = null,
    var url: String = "",
    private var mHeaders: LinkedList<Pair<String, String>> = LinkedList(),
    private var mParams: LinkedList<Pair<String, Any>> = LinkedList()
) {

     var fileDir: String = ""
     var fileName: String = ""
     var currentSize: Long = 0
     var totalSize: Long = 0
     var supportRange: Boolean = false
     var lastModify: String = ""
     var status: Int = 0
    var mDownloadCallback:DownloadCallback?=null

    init {
        currentSize = 0
        supportRange = true
        val externalFile = Kommon.context.getExternalFilesDir(null)
        fileDir = externalFile?.absolutePath?:""  + File.separator + "download"
        status = StatusConstant.NONE
    }

    fun setTag(tag: String): DownloadRequest {
        this.tag = tag
        return this
    }
    fun updateTag(): DownloadRequest {
        if (TextUtils.isEmpty(tag)) {
            this.tag = "download" + System.currentTimeMillis()
        }
        return this
    }
    fun headers(vararg headers: Pair<String, Any>): DownloadRequest {
        headers.forEach { this.mHeaders.add(Pair(it.first, "${it.second}")) }
        return this
    }

    fun headers(map: Map<String, Any>): DownloadRequest {
        map.forEach { this.mHeaders.add(Pair(it.key, "${it.value}")) }
        return this
    }

    fun header(key: String, value: Any): DownloadRequest {
        this.mHeaders.add(Pair(key, "$value"))
        return this
    }

    fun params(vararg params: Pair<String, Any>): DownloadRequest {
        params.forEach {
            this.mParams.add(Pair(it.first, it.second))
        }
        return this
    }

    fun params(map: Map<String, Any>): DownloadRequest {
        map.forEach {
            this.mParams.add(Pair(it.key, it.value))
        }
        return this
    }

    fun param(key: String, value: Any): DownloadRequest {
        this.mParams.add(Pair(key, value))
        return this
    }

    fun setFileName(fileName: String): DownloadRequest {
        this.fileName = fileName
        return this
    }

    fun setFileDir(fileDir: String): DownloadRequest {
        this.fileDir = fileDir
        return this
    }
    fun setCurrentSize(currentSize: Long): DownloadRequest {
        this.currentSize = currentSize
        return this
    }
    fun setTotalSize(totalSize: Long): DownloadRequest {
        this.totalSize = totalSize
        return this
    }

    fun setSupportRange(supportRange: Boolean): DownloadRequest {
        this.supportRange = supportRange
        return this
    }

    fun canStart(): Boolean {
        return status == StatusConstant.NONE || status == StatusConstant.WAITING
    }

    fun callback(callback:DownloadCallback):DownloadRequest{
        mDownloadCallback = callback
        return this
    }


    fun call():Call{
        val request = Request.Builder().url(url)
            .apply {
                SmartHttp.globalHeaders.forEach {
                    addHeader(it.key, it.value)
                }
                mHeaders.forEach {
                    addHeader(it.first, it.second)
                }
            }
            .get()
            .tag(tag!!)
            .build()

        return SmartHttp.provideNewDownloadClient().newCall(request)
    }

    fun start(){
        val task = DownloadManager.getDownloadTask(this)
        mDownloadCallback?.let {
            task.addCallback(it)
        }
        task.start()
    }

    fun prepare():Observable<DownloadRequest>{
        return Observable.create(RequestInitSubscribe(this))
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }



}