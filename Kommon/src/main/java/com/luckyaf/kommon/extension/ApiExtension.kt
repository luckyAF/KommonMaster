package com.luckyaf.kommon.extension

import android.os.Build

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/3
 *
 */
inline fun aboveApi(api: Int,  block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= api ) {
        block()
    }
}

inline fun belowApi(api: Int,  block: () -> Unit) {
    if (Build.VERSION.SDK_INT <= api) {
        block()
    }
}