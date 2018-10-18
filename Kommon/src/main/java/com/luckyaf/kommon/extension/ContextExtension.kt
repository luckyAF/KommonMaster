package com.luckyaf.kommon.extension

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast
import com.luckyaf.kommon.utils.ToastUtil

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/12
 *
 */

fun Context.toastShort(msg:String){
    ToastUtil.show(this,msg)
}

fun Context.toastLong(msg:String){
    ToastUtil.show(this,msg,Toast.LENGTH_LONG)
}

fun Context.toastShort(@StringRes id:Int){
    ToastUtil.show(this,id)
}

fun Context.toastLong(@StringRes id:Int){
    ToastUtil.show(this,id,Toast.LENGTH_LONG)
}