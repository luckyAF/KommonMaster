package com.luckyaf.kommon.net

import java.io.IOException

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/25
 *
 */
interface HttpCallback<T>{
    fun onSuccess(data: T)
    fun onError(e: IOException){}
}