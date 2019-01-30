package com.luckyaf.kommon.net

import java.io.IOException

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/30
 *
 */
class CommonCallback<T> :HttpCallback<T>{
    private var success: ((t: T) -> Unit)? = null
    private var error: ((e: IOException) -> Unit)? = null
    private var cancel:(() -> Unit)?=null
    private var netError :(() -> Unit)?=null
    private var timeOut :(() -> Unit)?=null


    fun success(s: ((t: T) -> Unit)) {
        success = s
    }

    fun error(e: ((t: IOException) -> Unit)) {
        error = e
    }
    fun cancel(c: (() -> Unit)) {
        cancel = c
    }

    fun netError(n: (() -> Unit)) {
        netError = n
    }

    fun timeOut(t:(() -> Unit)){
        timeOut = t
    }


    override fun onSuccess(data: T) {
        success?.invoke(data)
    }

    override fun onError(e: IOException) {
        error?.invoke(e)
    }

    override fun onCancel(){
        cancel?.invoke()
    }

    override fun onNetError() {
        netError?.invoke()
    }

    override fun onTimeOut() {
        timeOut?.invoke()
    }
}


