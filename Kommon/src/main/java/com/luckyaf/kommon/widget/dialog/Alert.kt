package com.luckyaf.kommon.widget.dialog

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.View
import com.luckyaf.kommon.R

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-19
 *
 */

typealias OnTypeClickListener = (type: Int, msg: String) -> Unit

@Suppress("unused")
object Alert {
    const val CONFIRM = 1
    const val CANCEL = 0


    private var mDialog: AlertDialog? = null

    //正常弹出，带有确定和取消，例子：
    /**
     * Alert.normal(self,"title","text",ok = "confirm"){
    type, msg ->

    }
     */
    fun normal(
            src: Context,
            title: String,
            content: String,
            cancel: String = "取消",
            ok: String = "确定",
            clickListener: OnTypeClickListener?
    ) {
        AlertDialog.Builder(src)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(ok) { _, _ -> clickListener?.invoke(CONFIRM, "") }
                .setNegativeButton(cancel) { _, _ -> clickListener?.invoke(CANCEL, "") }
                .create()
                .show()
    }


    fun confirm(
            src: Context,
            title: String,
            content: String,
            ok: String = "确定",
            clickListener: OnTypeClickListener?=null
    ) {
        val builder = AlertDialog.Builder(src)
        if(title.isNotEmpty())    builder.setTitle(title)
        builder.setMessage(content)
                .setCancelable(false)
                .setPositiveButton(ok) { _, _ -> clickListener?.invoke(CONFIRM, "") }
                .create().show()
    }


    fun ctreateLoadingDialog(context:Context ,desc: String ):AlertDialog{
        val view = View.inflate(context, R.layout.dialog_kommon_loading, null)
        val alertDialog = AlertDialog.Builder(context).setView(view).setTitle(desc).create()
        alertDialog.setCanceledOnTouchOutside(false)
        return alertDialog
    }
}