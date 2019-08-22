package com.luckyaf.kommon.constant

import android.support.annotation.IntDef


/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
object MessageType {
    /**
     * 提示
     */
    const val PROMPT  =  1
    /**
     * 消息
     */
    const val   INFO = 2
    /**
     * 警告
     */
    const val WARNING = 3
    /**
     * 危险
     */
    const val DANGEROUS = 4


    @IntDef(PROMPT, INFO, WARNING, DANGEROUS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MessageType
}
