package com.luckyaf.kommon.widget

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


    fun confirm(context: Context,
                title: String,
                content: String,
                yes:String = "确定",
                no:String = "取消",
                confirm : () -> Unit,
                cancel : () -> Unit
                ){
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(yes) { _, _ -> confirm() }
                .setNegativeButton(no) { _, _ -> cancel() }
                .create()
                .show()
    }
    fun confirm(context: Context,
                title: String,
                content: String,
                yes:String = "确定",
                confirm : () -> Unit
    ){
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(yes) { _, _ -> confirm() }
                .create()
                .show()
    }




    fun createLoadingDialog(context:Context ,desc: String ):AlertDialog{
        val view = View.inflate(context, R.layout.dialog_kommon_loading, null)
        val alertDialog = AlertDialog.Builder(context).setView(view).setTitle(desc).create()
        alertDialog.setCanceledOnTouchOutside(false)
        return alertDialog
    }
}