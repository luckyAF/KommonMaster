package com.luckyaf.kommon.model

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/12
 *
 */
class BaseResponse<T>(val code: Int,
                      val message: String,
                      val data: T)