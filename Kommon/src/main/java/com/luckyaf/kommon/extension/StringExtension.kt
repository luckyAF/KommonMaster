package com.luckyaf.kommon.extension

import java.util.regex.Pattern

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.ParcelableSpan
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.luckyaf.kommon.manager.ActivityManager
import java.util.*

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */


private val floatPattern = Pattern.compile("^[-\\+]?[.\\d]*$")
private val intPattern = Pattern.compile("^[-\\+]?[\\d]*$")


fun String?.isInteger(): Boolean {
    return if (this.isNullOrEmpty()) {
        false
    } else intPattern.matcher(this).matches()
}

fun String?.isFloat(): Boolean {
    return if (this.isNullOrEmpty()) {
        false
    } else floatPattern.matcher(this).matches()
}

fun String?.isNumber(): Boolean {
    return isInteger() || isFloat()
}


fun String.safeSubstring(startIndex: Int, endIndex: Int = this.length): String {
    if (startIndex > length) {
        return ""
    }
    if (endIndex < 0) {
        return ""
    }
    val start = if (startIndex < 0) {
        0
    } else {
        startIndex
    }
    val end = if (endIndex > length) {
        length
    } else {
        endIndex
    }
    return if (start > end) {
        this.substring(end, start).reversed()
    } else {
        this.substring(start, end)
    }
}


private fun CharSequence.setSpan(span: ParcelableSpan, start: Int, end: Int): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    return spannableString
}

fun CharSequence.formatStringColor(color: Int, start: Int, end: Int): SpannableString {
    return this.setSpan(ForegroundColorSpan(ContextCompat.getColor(ActivityManager.instance.getNowContext(), color)), start, end)
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
