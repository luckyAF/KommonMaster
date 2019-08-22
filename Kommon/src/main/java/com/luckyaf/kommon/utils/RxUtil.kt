package com.luckyaf.kommon.utils

import android.widget.TextView
import com.luckyaf.kommon.utils.internal.TextChangeObservable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */

object RxUtil {

    fun loopDoing(interval: Int, unit: TimeUnit, block:()->Unit): Disposable {
        return Observable.interval(interval.toLong(), unit)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    block.invoke()
                }
    }


    /**
     * 监听text变化
     *
     * @param textView textView
     * @return
     */
    fun textChange(textView: TextView): Observable<CharSequence> {
        return TextChangeObservable(textView)
    }


}