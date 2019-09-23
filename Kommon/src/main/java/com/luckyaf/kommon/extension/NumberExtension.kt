package com.luckyaf.kommon.extension

import java.text.DecimalFormat
import kotlin.math.floor

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */


fun Number.format(format: String): String {
    return String.format(format, this)
}

val twoDigitFormat = DecimalFormat("0.##")
val threeDigitFormat = DecimalFormat("0.###")
val fourDigitFormat = DecimalFormat("0.####")
val fiveDigitFormat = DecimalFormat("0.#####")
val sixDigitFormat = DecimalFormat("0.######")

val twoZeroFormat = DecimalFormat("0.00")
val threeZeroFormat = DecimalFormat("0.000")
val fourZeroFormat = DecimalFormat("0.0000")
val fiveZeroFormat = DecimalFormat("0.00000")
val sixZeroFormat = DecimalFormat("0.000000")

// 精度范围
private const val eps = 1e-10

fun Double.isInteger():Boolean{
    return this - floor(this) < eps
}

fun Float.isInteger():Boolean{
    return this - floor(this) < eps
}

fun Double.isZero():Boolean{
    return absoluteValue() < eps
}
fun Float.isZero():Boolean{
    return absoluteValue() < eps
}

fun Double.absoluteValue():Double{
    return if(this > 0 ){ this} else {-this}
}
fun Float.absoluteValue():Float{
    return if(this > 0 ){ this} else {-this}
}

/**
 * 变成整数
 */
fun Number.keepToInt(): String {
    return this.toInt().toString()
}

/**
 * 保留两位 是否填充0
 */
fun Number.keepTwoDigit(fillZero:Boolean = false): String {
    return if(fillZero){
        twoZeroFormat.format(this)
    }else{
        twoDigitFormat.format(this)
    }
}

/**
 * 保留三位
 */
fun Number.keepThreeDigit(fillZero:Boolean = false): String {
    return if(fillZero){
        threeZeroFormat.format(this)
    }else{
        threeDigitFormat.format(this)
    }
}

/**
 * 保留四位
 */
fun Number.keepFourDigit(fillZero:Boolean = false): String {
    return if(fillZero){
        fourZeroFormat.format(this)
    }else{
        fourDigitFormat.format(this)
    }
}

/**
 * 保留五位
 */
fun Number.keepFiveDigit(fillZero:Boolean = false): String {
    return if(fillZero){
        fiveZeroFormat.format(this)
    }else{
        fiveDigitFormat.format(this)
    }
}

/**
 * 保留六位
 */
fun Number.keepSixDigit(fillZero:Boolean = false): String {
    return if(fillZero){
        sixZeroFormat.format(this)
    }else{
        sixDigitFormat.format(this)
    }
}



