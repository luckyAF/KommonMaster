package com.luckyaf.kommon.extension

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */

fun CharSequence?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()

fun CharSequence?.isNullOrBlank(): Boolean = this == null || this.isBlank()
