package com.luckyaf.kommon.extension

import com.luckyaf.kommon.utils.TransformUtil
import com.tencent.mmkv.MMKV

/**
 * 类描述：存储相关  用了腾讯开源的mmkv
 * @author Created by luckyAF on 2019-02-25
 * 需要
 * "com.tencent:mmkv"
 * MMKV.initialize(this)
 */

fun <T> T.saveToCache(key: String) {
    val kv = MMKV.defaultMMKV()
    when (this) {
        is Boolean -> kv.putBoolean(key, this)
        is Int -> kv.putInt(key, this)
        is Long -> kv.putLong(key, this)
        is Float -> kv.putFloat(key, this)
        is Double -> kv.encode(key, this)
        is String -> kv.putString(key, this)
        else -> kv.putString(key, TransformUtil.serialize(this))
    }
}


fun <T> saveToCache(key: String, value: T) {
    val kv = MMKV.defaultMMKV()
    when (value) {
        is Boolean -> kv.putBoolean(key, value)
        is Int -> kv.putInt(key, value)
        is Long -> kv.putLong(key, value)
        is Float -> kv.putFloat(key, value)
        is Double -> kv.encode(key, value)
        is String -> kv.putString(key, value)
        else -> kv.putString(key, TransformUtil.serialize(value))
    }
}
@Suppress("UNCHECKED_CAST")
fun <T> getFromCache(key: String, default: T): T {
    val kv = MMKV.defaultMMKV()
    if (!kv.containsKey(key)) {
        return default
    }
    return when (default) {
        is Boolean -> kv.decodeBool(key, default) as T
        is Int -> kv.decodeInt(key, default) as T
        is Long -> kv.decodeLong(key, default) as T
        is Float -> kv.decodeFloat(key, default) as T
        is Double -> kv.decodeDouble(key, default) as T
        is String -> kv.decodeString(key, default) as T
        else -> TransformUtil.deSerialization<T>(kv.decodeString(key)) ?: default
    }
}
