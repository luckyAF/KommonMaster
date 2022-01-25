package com.luckyaf.kommon.callback

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

/**
 * 类描述：
 * @author Created by luckyAF on 2020/9/28
 *
 */

class DestroyCallback(val block:()->Unit): LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        block.invoke()
    }
}

interface LifecycleCallback{
    fun onCreate(){}
    fun onStart(){}
    fun onResume(){}
    fun onPause(){}
    fun onStop(){}
    fun onDestroy(){}
    fun onAny(){}
}

class LifecycleListener(private val callback: LifecycleCallback)
    : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        callback.onCreate()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        callback.onStart()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        callback.onResume()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        callback.onPause()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        callback.onStop()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        callback.onDestroy()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny() {
        callback.onAny()
    }
}