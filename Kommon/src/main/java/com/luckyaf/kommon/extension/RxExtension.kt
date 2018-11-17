package com.luckyaf.kommon.extension

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */


fun <T, R> T.executeIO(block : T.() -> R, rtn : (R) -> Unit): Disposable{
     return Observable.create<R>{ it.onNext(block()) }
            .dispatchDefault()
            .subscribe{rtn(it)}
            //.addToComposite(null)// 可以不加这个 但是一直提示好烦
}



fun <T> Observable<T>.applySchedulers():Observable<T>{
    return subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun  Disposable.addToComposite(disposables: CompositeDisposable?){
    disposables?.add(this)
}


fun <T> Observable<T>.dispatchDefault(): Observable<T> =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.dispatchDefault(): Single<T> =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


fun <T> Flowable<T>.dispatchDefault(): Flowable<T> =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun Completable.dispatchDefault(): Completable =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())