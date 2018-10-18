package com.luckyaf.kommon.extension

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */


fun <T, R> T.executeIO(block : T.() -> R, rtn : (R) -> Unit) : T{
     Observable.create<R>{ it.onNext(block()) }
            .applySchedulers()
            .subscribe{rtn(it)}
            .addToComposite(null)// 可以不加这个 但是一直提示好烦
    return this
}



fun <T> Observable<T>.applySchedulers():Observable<T>{
    return subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun  Disposable.addToComposite(disposables: CompositeDisposable?){
    disposables?.add(this)
}