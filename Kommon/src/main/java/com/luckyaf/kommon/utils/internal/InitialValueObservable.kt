package com.luckyaf.kommon.utils.internal

import io.reactivex.Observable
import io.reactivex.Observer

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
abstract class InitialValueObservable<T> : Observable<T>() {
    protected  var initialValue: T?=null

    override fun subscribeActual(observer: Observer<in T>) {
        subscribeListener(observer)
        initialValue?.let {
            observer.onNext(it)
        }
    }
    protected abstract fun subscribeListener(observer: Observer<in T>)

    internal fun skipInitialValue(): Observable<T> {
        return Skipped()
    }
    internal inner class Skipped : Observable<T>() {
        override fun subscribeActual(observer: Observer<in T>) {
            subscribeListener(observer)
            initialValue?.let {
                observer.onNext(it)
            }
        }
    }
}
