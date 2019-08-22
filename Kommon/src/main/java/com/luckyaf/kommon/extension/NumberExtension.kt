package com.luckyaf.kommon.extension

import java.text.DecimalFormat
import kotlin.math.floor

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */


fun Double.format(format: String): String {
    return String.format(format, this)
}

val twoDigitFormat = DecimalFormat("###.##")
val threeDigitFormat = DecimalFormat("###.###")
val fourDigitFormat = DecimalFormat("###.####")
val fiveDigitFormat = DecimalFormat("###.#####")
val sixDigitFormat = DecimalFormat("###.######")

// 精度范围
private const val eps = 1e-10

fun Double.isInteger():Boolean{
    return this - floor(this) < eps
}

fun Double.isZero():Boolean{
    return absoluteValue() < eps
}

fun Double.absoluteValue():Double{
    return if(this > 0 ){ this} else {-this}
}

/**
 * 变成整数
 */
fun Double.keepToInt(): String {
    return this.toInt().toString()
}

/**
 * 保留两位
 */
fun Double.keepTwoDigit(): String {
    return twoDigitFormat.format(this)
}

/**
 * 保留三位
 */
fun Double.keepThreeDigit(): String {
    return threeDigitFormat.format(this)
}

/**
 * 保留四位
 */
fun Double.keepFourDigit(): String {
    return fourDigitFormat.format(this)
}

/**
 * 保留五位
 */
fun Double.keepFiveDigit(): String {
    return fiveDigitFormat.format(this)
}

/**
 * 保留六位
 */
fun Double.keepSixDigit(): String {
    return sixDigitFormat.format(this)
}

