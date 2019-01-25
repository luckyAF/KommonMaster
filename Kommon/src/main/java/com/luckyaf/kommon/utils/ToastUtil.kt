package com.luckyaf.kommon.utils

import android.content.Context
import android.widget.Toast

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
object ToastUtil {

    private var TOAST: Toast? = null

    fun show(context: Context?, resourceID: Int) {
        show(context, resourceID, Toast.LENGTH_SHORT)
    }

    fun show(context: Context?, text: String) {
        show(context, text, Toast.LENGTH_SHORT)
    }

    fun show(context: Context?, resourceID: Int, duration: Int) {
        context ?: return
        val text = context.resources.getString(resourceID)
        show(context, text, duration)
    }

    fun show(context: Context?, text: String, duration: Int) {
        context ?: return
        if (TOAST == null) {
            TOAST = Toast.makeText(context, text, duration)
        } else {
            TOAST?.cancel()
            TOAST?.setText(text)
        }
        TOAST?.show()
    }


}