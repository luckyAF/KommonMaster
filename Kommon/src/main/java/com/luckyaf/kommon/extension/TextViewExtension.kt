package com.luckyaf.kommon.extension

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/3
 *
 */

fun EditText.addEnterListener(block:()->Unit){
    this.setOnEditorActionListener { _, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode
                        && KeyEvent.ACTION_DOWN == event.action)) {
            block()
        }
        false
    }
}


fun LifecycleOwner.watchText(textView: TextView,rtn:(String)-> Unit){
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            rtn.invoke(s.toString())
        }
        override fun afterTextChanged(s: Editable) {
        }
    }
    textView.addTextChangedListener(textWatcher)
    lifecycle.addObserver(TextWatcherLifecycleListener(textView,textWatcher, lifecycle))
}

internal class TextWatcherLifecycleListener(private val textView:TextView,private val listener:TextWatcher, private val lifecycle: Lifecycle):
        LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        textView.removeTextChangedListener(listener)
        lifecycle.removeObserver(this)
    }
}
