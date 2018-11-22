package com.luckyaf.kommon.widget.recyclerview

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
interface MultipleType <in T> {
    fun getLayoutId(item: T, position: Int): Int
}