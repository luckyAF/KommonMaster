package com.luckyaf.kommon.extension

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/3
 *
 */


fun  <T:Any> T.addTo(collection: MutableCollection<T>?){
    collection?.add(this)
}


fun <T:Any> Array<T>.addAllTo(collection: MutableCollection<T>?){
    collection?.addAll(this)
}

fun <T:Any> Collection<T>.addAllTo(collection: MutableCollection<T>?){
    collection?.addAll(this)
}

fun <T:Any> T.isMemberOf(collection: MutableCollection<T>?) : Boolean{
    collection?:return false
    return collection.contains(this)

}


@Suppress("UNCHECKED_CAST")
fun Bundle.put(params: Array<out Pair<String, Any>>): Bundle {

    params.forEach {
        val key = it.first
        val value = it.second
        when (value) {
            is Int -> putInt(key, value)
            is IntArray -> putIntArray(key, value)
            is Long -> putLong(key, value)
            is LongArray -> putLongArray(key, value)
            is CharSequence -> putCharSequence(key, value)
            is String -> putString(key, value)
            is Float -> putFloat(key, value)
            is FloatArray -> putFloatArray(key, value)
            is Double -> putDouble(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is Char -> putChar(key, value)
            is CharArray -> putCharArray(key, value)
            is Short -> putShort(key, value)
            is ShortArray -> putShortArray(key, value)
            is Boolean -> putBoolean(key, value)
            is BooleanArray -> putBooleanArray(key, value)

            is Serializable -> putSerializable(key,value)
            is Parcelable -> putParcelable(key, value)
            is Bundle -> putAll(value)
            is Array<*> -> when {

                value.isArrayOf<Parcelable>() -> putParcelableArray(key, value as Array<out Parcelable>?)
            }

        }
    }
    return this
}