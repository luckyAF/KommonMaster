package com.luckyaf.kommon.http.callback


/**
 * 类描述：请求回调
 * @author Created by luckyAF on 2019-08-03
 *
 */
interface HttpCallback<T>{
    fun onSuccess(data: T?)
    fun onError(e: Exception){}
    fun onCancel(){}
}