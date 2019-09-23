package com.luckyaf.kommon.http.download

import android.support.v4.util.ArrayMap
import com.luckyaf.kommon.http.request.DownloadRequest

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
object DownloadManager {
    private val downloadTasks = ArrayMap<Any, DownloadTask>()//用来存放请求
    private var requestUpdater:((DownloadRequest) -> Unit ) ?= null

    fun setUpdater(updater: (DownloadRequest) -> Unit ){
        requestUpdater = updater
    }

    /**
     * 从本地保存的下载信息 放入下载管理
     */
    fun initRequests(vararg request: DownloadRequest){
        request.iterator().forEach {
            if(!downloadTasks.containsKey(it.tag)){
                downloadTasks[it.tag] = DownloadTask.createTask(it)
            }
        }
    }


    fun getDownloadTask(request: DownloadRequest): DownloadTask {
        return if (downloadTasks.containsKey(request.tag)) {
            downloadTasks[request.tag]!!
        } else {
            val downloadTask = DownloadTask.createTask(request)
            downloadTasks[request.tag] = downloadTask
            downloadTask
        }
    }


    fun cancelTask(tag: Any) {
        if (downloadTasks.containsKey(tag)) {
            val downloadTask = downloadTasks[tag]
            downloadTask!!.pause()
        }
    }

    fun removeTask(tag: Any) {
        if (downloadTasks.containsKey(tag)) {
            val downloadTask = downloadTasks[tag]
            downloadTask!!.clear()
            downloadTasks.remove(tag)
        }
    }


    fun updateDownloadRequest(request: DownloadRequest){
        requestUpdater?.invoke(request)
    }


}