package com.luckyaf.kommon.http.callback


/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-03
 *
 */
class CommonCallback<T> : HttpCallback<T> {
    private var success: ((t: T?) -> Unit)? = null
    private var error: ((e: Exception) -> Unit)? = null
    private var cancel: (() -> Unit)? = null

    fun success(s: ((t: T?) -> Unit)) {
        success = s
    }
    fun error(e: ((t: Exception) -> Unit)) {
        error = e
    }
    fun cancel(c: (() -> Unit)) {
        cancel = c
    }


    override fun onSuccess(data: T?) {
        success?.invoke(data)
    }

    override fun onError(e: Exception) {
        error?.invoke(e)
    }

    override fun onCancel() {
        cancel?.invoke()
    }

}