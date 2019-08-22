package com.luckyaf.kommon.extension

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
fun <R> launchIO(
        block: suspend CoroutineScope.() -> R,
        rtn: (R) -> Unit,
        error: ((Exception) -> Unit) = {}
): Job {
    return GlobalScope.launch {
        try {
            val data = withContext(Dispatchers.IO) {
                block()
            }
            withContext(Dispatchers.Main) {
                rtn(data)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                error.invoke(e)
            }
        }
    }
}

fun runOnMainScope(block: () -> Unit) {
    GlobalScope.launch {
        withContext(Dispatchers.Main) {
            block()
        }
    }
}

fun runOnIOScope(block: () -> Unit) {
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}

fun runOnCPUScope(block: () -> Unit) {
    GlobalScope.launch {
        withContext(Dispatchers.Default) {
            block()
        }
    }
}
fun postDelayed(delayMillis: Long, task: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(task, delayMillis)
}


/**
 * execute in main thread
 * @param start doSomeThing first
 */
infix fun LifecycleOwner.start(start: (() -> Unit)): LifecycleOwner{
    GlobalScope.launch(Main) {
        start()
    }
    return this
}

/**
 * execute in io thread pool
 * @param loader http request
 * @param needAutoCancel need to cancel when activity destroy
 */
fun <T> LifecycleOwner.request(loader: suspend () -> T): Deferred<T> {
    return  request(loader,true)
}


/**
 * execute in io thread pool
 * @param loader http request
 * @param needAutoCancel need to cancel when activity destroy
 */
fun <T> LifecycleOwner.request(loader: suspend () -> T, needAutoCancel: Boolean = true): Deferred<T> {
    val deferred = GlobalScope.async(Dispatchers.IO, start = CoroutineStart.LAZY) {
        loader()
    }
    if(needAutoCancel){
        lifecycle.addObserver(CoroutineLifecycleListener(deferred, lifecycle))
    }
    return deferred
}

internal class CoroutineLifecycleListener(private val deferred: Deferred<*>, private val lifecycle: Lifecycle):
        LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        if (deferred.isActive) {
            deferred.cancel()
        }
        lifecycle.removeObserver(this)
    }
}


/**
 * execute in main thread
 * @param onSuccess callback for onSuccess
 * @param onError callback for onError
 * @param onComplete callback for onComplete
 */
fun <T> Deferred<T>.then(onSuccess: suspend (T) -> Unit={} , onError: suspend (String) -> Unit= {}, onComplete: (() -> Unit) = {}): Job {
    return GlobalScope.launch(context = Main) {
        try {
            val result = this@then.await()
            onSuccess(result)
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is UnknownHostException -> onError("请求服务器异常")
                is TimeoutException -> onError("请求超时")
                is SocketTimeoutException -> onError("")
                else -> onError(e.message?:"")
            }
        }finally {
            onComplete.invoke()
        }
    }
}