package com.luckyaf.kommon.utils

import android.os.Handler
import android.os.Looper

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
object ThreadUtil {

    fun onMainThread(runnable: Runnable) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(runnable)
    }
}