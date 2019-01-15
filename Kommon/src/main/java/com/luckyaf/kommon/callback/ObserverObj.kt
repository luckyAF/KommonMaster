package com.luckyaf.kommon.callback

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/11
 *
 */
@Suppress("unused")
class ObserverObj<T> : Observer<T> {

    private var _a: ((disposable: Disposable) -> Unit)? = null
    private var _b: ((t: T) -> Unit)? = null
    private var _c: (() -> Unit)? = null
    private var _d: ((e: Throwable) -> Unit)? = null
    private lateinit var disposable: Disposable

    fun _onSubscribe(t: ((disposable: Disposable) -> Unit)) {
        _a = t
    }

    fun _onNext(t: ((t: T) -> Unit)) {
        _b = t
    }
    fun _onComplete(t: (() -> Unit)) {
        _c = t
    }

    fun _onError(t: ((e: Throwable) -> Unit)) {
        _d = t
    }

    override fun onSubscribe(d: Disposable) {
        disposable = d
        _a?.invoke(d)
    }

    override fun onNext(t: T) {
        _b?.invoke(t)
    }

    override fun onComplete() {
        _c?.invoke()
    }

    override fun onError(e: Throwable) {
        _d?.invoke(e)
    }

    fun getDisposable(): Disposable {
        return disposable
    }
}

inline fun <reified T> Observable<T>._subscribe(func: ObserverObj<T>.() -> Unit): Disposable {
    val real = ObserverObj<T>()
    real.func()
    subscribe(real)
    return real.getDisposable()
}
