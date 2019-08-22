package com.luckyaf.kommon.http.download

import com.luckyaf.kommon.callback._subscribe
import com.luckyaf.kommon.extension.no
import com.luckyaf.kommon.http.callback.DownloadCallback
import com.luckyaf.kommon.http.internal.Progress
import com.luckyaf.kommon.http.request.DownloadRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.ArrayList

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
data class DownloadTask(
    private val downloadRequest: DownloadRequest
) {
    companion object {
        fun createTask(downloadRequest: DownloadRequest): DownloadTask {
            return DownloadTask(downloadRequest)
        }
        fun createTask(url:String):DownloadTask{
            return DownloadTask(DownloadRequest(url))
        }
    }
    private var limitSpeed: Int = Int.MAX_VALUE    // 最高速度限制  kb/s
    private var notifyInterval: Int = 300 // 通知间隔     ms


    private var disposable: Disposable? = null

    private val downloadCallbackList = ArrayList<DownloadCallback>()

    init {

    }

    /**
     * 不支持实时 修改下载速度
     */
    fun setMaxSpeed(speed: Int): DownloadTask {
        if (speed > 0) {
            this.limitSpeed = speed
        }
        return this
    }

    fun setNotifyInterval(interval: Int): DownloadTask {
        if (interval > 0) {
            this.notifyInterval = interval
        }
        return this
    }



    fun start() {
        if (null == disposable) {
            onBackStart()
            disposable = Observable.create(DownloadSubscribe(downloadRequest)
                    .setNotifyInterval(notifyInterval)
                    .setSpeedLimit(limitSpeed))
                .subscribeOn(Schedulers.io())//在子线程执行
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    ._subscribe {
                        _onNext {
                            onBackProgress(it)
                        }
                        _onError {
                            onBackError(it)
                        }
                        _onComplete {
                            onBackFinish()
                        }
                    }

        }
    }

    private fun onBackStart(){
        downloadCallbackList.forEach {
            it.onStart()
        }
    }

   private fun onBackProgress(progress: Progress){
       downloadCallbackList.forEach {
           it.onProgress(progress)
       }
   }

    private fun onBackError(throwable: Throwable){
        downloadCallbackList.forEach {
            it.onError(Exception(throwable.message))
        }
    }
    private fun onBackFinish(){
        val file = File(downloadRequest.fileDir, downloadRequest.fileName)
        downloadCallbackList.forEach {
            it.onFinish(file)
        }
    }

    fun addCallback(callback: DownloadCallback){
        downloadCallbackList.add(callback)
    }

    fun clear(){
        downloadCallbackList.clear()
        disposable?.isDisposed?.no {
            disposable?.dispose()
        }
        disposable = null
    }

    fun pause() {
        disposable?.isDisposed?.no {
            disposable?.dispose()
        }
        disposable = null
    }
}



