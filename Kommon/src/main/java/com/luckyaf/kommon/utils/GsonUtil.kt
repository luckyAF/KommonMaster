package com.luckyaf.kommon.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/6
 *
 */
object GsonUtil {

    val gson by lazy { privderGson() }

    fun privderGson(): Gson {
        return GsonBuilder().create()
    }
}