package com.luckyaf.kommon.utils.internal

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
class TextChangeObservable(private val mTextView: TextView) : InitialValueObservable<CharSequence>() {

    init {
        initialValue = mTextView.text
    }
    override fun subscribeListener(observer: Observer<in CharSequence>) {
        val listener = Listener(mTextView, observer)
        observer.onSubscribe(listener)
        mTextView.addTextChangedListener(listener)
    }


    internal inner class Listener(private val mView: TextView, private val mObserver: Observer<in CharSequence>) : MainThreadDisposable(), TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!isDisposed) {
                mObserver.onNext(s)
            }
        }

        override fun afterTextChanged(s: Editable) {

        }

        override fun onDispose() {
            mView.removeTextChangedListener(this)
        }
    }

}
