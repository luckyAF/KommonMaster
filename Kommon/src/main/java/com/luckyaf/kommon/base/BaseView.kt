package com.luckyaf.kommon.base

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.luckyaf.kommon.constant.MessageType

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
interface BaseView : LifecycleOwner {


    /**
     * 获取 Activity 对象
     *
     * @return activity
     */
    fun  getSelfActivity(): Activity



    /**
     * 显示信息
     *
     * @param message 消息内容
     */
    fun showMessage(message: String, @MessageType.MessageType messageType: Int = MessageType.PROMPT)

    /**
     * 显示加载中
     */
    fun showLoading(message:String = "")

    /**
     * 隐藏加载中
     */
    fun hideLoading()


}