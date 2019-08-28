package com.luckyaf.kommon.mvi

import com.luckyaf.kommon.constant.MessageType

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-28
 *
 */
open class CommonUIEvent {
    data class Message(val message: String, @MessageType.MessageType val messageType: Int = MessageType.PROMPT) : CommonUIEvent()
    data class Loading(val show: Boolean, val message: String = "") : CommonUIEvent()
}
