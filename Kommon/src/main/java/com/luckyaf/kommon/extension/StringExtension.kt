package com.luckyaf.kommon.extension

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.ParcelableSpan
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.luckyaf.kommon.manager.ActivityManager

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */

fun Any?.toValueString():String{
    this ?: return "null"
    return this.toString()
}


fun CharSequence?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun CharSequence?.isNullOrBlank(): Boolean = this == null || this.isBlank()

private fun CharSequence.setSpan(span: ParcelableSpan, start: Int, end: Int): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    return spannableString
}

fun CharSequence.formatStringColor(color: Int, start: Int, end: Int): SpannableString {
    return this.setSpan(ForegroundColorSpan(ContextCompat.getColor(ActivityManager.instance.getNowContext(),color)), start, end)
}
/**
 * 换色四连
 */
fun CharSequence.setLogo(): CharSequence {
    return this.formatStringColor(Color.BLUE, 0, 1)
            .formatStringColor(Color.RED, 1, 2)
            .formatStringColor(Color.YELLOW, 2, 3)
            .formatStringColor(Color.GREEN, 3, 4)
}