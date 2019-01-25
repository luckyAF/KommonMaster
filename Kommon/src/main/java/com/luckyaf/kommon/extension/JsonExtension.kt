package com.luckyaf.kommon.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
inline fun <reified T : Any> Gson.fromJsonNew(json: String): T {
    return fromJson(json, T::class.java)
}

inline fun <reified T : Any> Gson.fromJsonData(data: Any?): T? {
    var result: T? = null
    try {
        result = fromJson(toJson(data), T::class.java)
    } catch (e: Throwable) {

    }
    return result
}

fun Any.toJson() = Gson().toJson(this)

inline fun <reified T> String.toJavaBean() = Gson().fromJson<T>(this,object : TypeToken<T>(){}.type)






