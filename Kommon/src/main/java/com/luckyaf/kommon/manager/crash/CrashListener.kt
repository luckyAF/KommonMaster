package com.luckyaf.kommon.manager.crash

import androidx.appcompat.app.AppCompatActivity


/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/16
 *
 */
interface CrashListener {

    /**
     * Handle crash in Ui thread, but if crash happens in lifecycle method, this callback will not be invoked
     * @param t        error
     * @param activity current activity
     */
    fun handleCrashInUiThread(t: Throwable?, activity: AppCompatActivity)

    /**
     * Handle crash in async thread
     */
    fun handleCrashInAsync(t: Throwable?)
}