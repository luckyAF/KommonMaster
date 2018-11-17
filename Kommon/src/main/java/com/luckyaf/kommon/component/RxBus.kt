package com.luckyaf.kommon.component

import io.reactivex.Observable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/18
 *
 */
object RxBus {
    // 支持背压且线程安全的，保证线程安全需要调用 toSerialized() 方法
    private val mBus: FlowableProcessor<Any>
            by lazy { PublishProcessor.create<Any>().toSerialized() }

    //发送事件
    fun post(obj: Any) {
        mBus.onNext(obj)
    }

    //订阅事件
    fun <T> toFlowable(tClass: Class<T>) = mBus.ofType(tClass)

    fun toFlowable() = mBus

    fun hasSubscribers() = mBus.hasSubscribers()


    //不支持背压且线程安全的，保证线程安全需要调用 toSerialized() 方法
    private val mBusNB: Subject<Any>
            by lazy { PublishSubject.create<Any>().toSerialized() }

    //发送事件
    fun postNB(obj: Any) {
        mBusNB.onNext(obj)
    }

    //订阅事件
    fun <T> toObservable(tClass: Class<T>): Observable<T> = mBusNB.ofType(tClass)

    fun toObservable(): Observable<Any> = mBusNB

    fun hasObservers() = mBusNB.hasObservers()
}
