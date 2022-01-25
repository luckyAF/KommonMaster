package com.luckyaf.kommon.extension

import android.view.View


/**
 * 类描述：View 扩展
 * @author Created by luckyAF on 2018/10/10
 *
 */


/**
 *  上次点击事件
 */
private var View.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

/**
 *   响应间隔
 */
private var View.responseInterval: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }

private var View.triggerFirstTime: Long
    get() = if (getTag(1123460106) != null) getTag(1123460106) as Long else 0
    set(value) {
        setTag(1123460106, value)
    }
/**
 *   响应间隔
 */
private var View.timeLimit: Long
    get() = if (getTag(1123461125) != null) getTag(1123461125) as Long else -1
    set(value) {
        setTag(1123461125, value)
    }

private var View.targetClickTime:Int
    get() = if (getTag(1123461126) != null) getTag(1123461126) as Int else -1
    set(value) {
        setTag(1123461126, value)
    }




/**
 * 是否超过间隔
 */
private fun View.overstep(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= responseInterval) {
        triggerLastTime = currentClickTime
        flag = true
    }
    return flag
}

private fun View.upToGrade(needClick:Int):Boolean{
    targetClickTime += 1
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerFirstTime >= timeLimit) {
        triggerFirstTime = currentClickTime
        targetClickTime = 1
        return false
    }else{
        return targetClickTime == needClick
    }
}



/***
 * 防双击点击事件View扩展
 * @param time Long 间隔时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
@Suppress("UNCHECKED_CAST")
fun View.clickWithTrigger(time: Long = 600, block: (View) -> Unit){
    responseInterval = time
    setOnClickListener {
        if (overstep()) {
            block(it)
        }
    }
}

/***
 * 多次点击事件View扩展
 * @param time Long 间隔时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
@Suppress("UNCHECKED_CAST")
fun View.multiClick(time: Long = 600,number:Int=3, block: (View) -> Unit){
    timeLimit = time
    setOnClickListener {
        if (upToGrade(number)) {
            targetClickTime = 0
            block(it)
        }
    }
}