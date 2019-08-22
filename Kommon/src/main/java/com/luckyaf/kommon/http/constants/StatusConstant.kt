package com.luckyaf.kommon.http.constants

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
object StatusConstant{
    const val NONE = 0           //  刚刚创建时的状态
    const val WAITING = 1        //  未开始，等待
    const val LOADING = 2        //  上传或下载中
    const val ERROR = 3          //  错误
    const val FINISHED = 4       //  已完成
}