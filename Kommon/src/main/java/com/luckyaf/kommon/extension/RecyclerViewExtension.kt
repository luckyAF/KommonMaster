package com.luckyaf.kommon.extension


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */

inline val androidx.recyclerview.widget.RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is androidx.recyclerview.widget.LinearLayoutManager -> orientation
            is androidx.recyclerview.widget.GridLayoutManager -> orientation
            is androidx.recyclerview.widget.StaggeredGridLayoutManager -> orientation
            else -> -1
        }
    }

/**
 * 设置分割线
 * @param color 分割线的颜色，默认是#DEDEDE
 * @param size 分割线的大小，默认是1px
 * @param isReplace 是否覆盖之前的ItemDecoration，默认是true
 *
 */
fun androidx.recyclerview.widget.RecyclerView.divider(color: Int = Color.parseColor("#DEDEDE"), size: Int = 1, isReplace: Boolean = true): androidx.recyclerview.widget.RecyclerView {
    val decoration = androidx.recyclerview.widget.DividerItemDecoration(context, orientation)
    decoration.setDrawable(GradientDrawable().apply {
        setColor(color)
        shape = GradientDrawable.RECTANGLE
        setSize(size, size)
    })
    if(isReplace && itemDecorationCount>0){
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
    return this
}
fun androidx.recyclerview.widget.RecyclerView.vertical(spanCount: Int = 0, isStaggered: Boolean = false): androidx.recyclerview.widget.RecyclerView {
    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
    if (spanCount != 0) {
        layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, spanCount)
    }
    if (isStaggered) {
        layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
    }
    return this
}

fun androidx.recyclerview.widget.RecyclerView.horizontal(spanCount: Int = 0, isStaggered: Boolean = false): androidx.recyclerview.widget.RecyclerView {
    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
    if (spanCount != 0) {
        layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, spanCount, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
    }
    if (isStaggered) {
        layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL)
    }
    return this
}
