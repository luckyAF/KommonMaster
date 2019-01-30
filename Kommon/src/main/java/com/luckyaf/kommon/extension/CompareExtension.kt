package com.luckyaf.kommon.extension

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/29
 *
 */



fun <T:Number> Comparable<T>?.Equeals(data :T):Boolean{
    this?:return false
    return this == data
}

fun <T:Number> Comparable<T>?.MoreThan(data :T):Boolean{
    this?:return false
    return this > data
}

fun <T:Number> Comparable<T>?.NotMoreThan(data :T):Boolean{
    this?:return false
    return this <= data
}


fun <T:Number> Comparable<T>?.NotLessThan(data :T):Boolean{
    this?:return false
    return this >= data
}


fun <T:Number> Comparable<T>?.LessThan(data :T):Boolean{
    this?:return false
    return this < data
}


