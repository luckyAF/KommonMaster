package com.luckyaf.kommon.component

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 类描述：
 * @author Created by luckyAF on 2020/9/28
 *
 */
class CustomLiveEvent<T> : MutableLiveData<T>(){
    private val mPending = AtomicBoolean(false)
    fun setHandled(){
        mPending.set(false)
    }
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T>{
            if (mPending.get()) {
                observer.onChanged(it)
            }
        })
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        setValue(null)
    }
}