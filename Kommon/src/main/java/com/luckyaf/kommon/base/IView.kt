package com.luckyaf.kommon.base

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/31
 *
 */
interface IView{

    //显示加载提示
    fun loading()

    //结束加载提示
    fun stopLoad()

    /*
    code错误码；msg错误信息;
     */
    fun error(code: Int, msg: String)

}