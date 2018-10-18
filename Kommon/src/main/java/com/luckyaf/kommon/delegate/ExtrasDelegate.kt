package com.luckyaf.kommon.delegate

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlin.reflect.KProperty

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
class ExtrasDelegate<out T>(private val extraName: String, private val defaultValue: T) {
    private var extraValue: T? = null
    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        extraValue = getExtra(extraValue,extraName,thisRef)
        return extraValue ?: defaultValue
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        extraValue = getExtra(extraValue,extraName,thisRef)
        return extraValue ?: defaultValue
    }
}

fun <T> extraDelegate(extraName: String,defaultValue: T) = ExtrasDelegate(extraName,defaultValue)

fun extraDelegate(extraName: String) = ExtrasDelegate(extraName,null)

@Suppress("UNCHECKED_CAST")
private fun <T> getExtra(oldExtra : T?,extraName: String,thisRef: AppCompatActivity): T? =
        oldExtra ?: thisRef.intent?.extras?.get(extraName) as T?

@Suppress("UNCHECKED_CAST")
private fun <T> getExtra(oldExtra: T?, extraName: String, thisRef: Fragment): T? =
        oldExtra ?: thisRef.arguments?.get(extraName) as T?

