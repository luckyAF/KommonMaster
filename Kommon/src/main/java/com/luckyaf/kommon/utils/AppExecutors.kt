package com.luckyaf.kommon.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 类描述：线程调度
 * @author Created by luckyAF on 2019/1/9
 *
 */
open class AppExecutors constructor(
        private val diskIO: Executor = Executors.newSingleThreadExecutor(),
        private val networkIO: Executor = Executors.newFixedThreadPool(3),
        private val mainThread: Executor = MainThreadExecutor()
) {


    fun runOnIoThread(f: () -> Unit) {
        diskIO.execute(f)
    }

    fun runOnNetThread(f: () -> Unit) {
        networkIO.execute(f)
    }

    fun runOnMainThread(f: () -> Unit) {
        mainThread.execute(f)
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}



