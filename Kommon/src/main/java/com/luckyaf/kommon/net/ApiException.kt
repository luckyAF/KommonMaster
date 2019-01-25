package com.luckyaf.kommon.net

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
data class ApiException(val errorCode: Int, val errorMsg: String):RuntimeException()