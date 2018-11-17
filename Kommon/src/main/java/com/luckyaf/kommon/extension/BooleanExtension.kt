package com.luckyaf.kommon.extension

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/6
 *
 */

sealed class BooleanExt<out T>

object Otherwise : BooleanExt<Nothing>()
class WithData<T>(val data: T) : BooleanExt<T>()

inline fun <T> Boolean.yes(block: () -> T) =
        when {
            this -> WithData(block())
            else -> Otherwise
        }

inline fun <T> Boolean.no(block: () -> T) =
        when {
            this -> Otherwise
            else -> WithData(block())
        }

inline infix fun <T> BooleanExt<T>.otherwise(block: () -> T) =
        when (this) {
            is Otherwise -> block()
            is WithData -> this.data
        }

inline operator fun <T> Boolean.invoke(block: () -> T) = yes(block)
