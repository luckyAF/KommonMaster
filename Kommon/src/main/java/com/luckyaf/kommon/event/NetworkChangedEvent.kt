package com.luckyaf.kommon.event

import com.luckyaf.kommon.constants.NetState

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/2
 *
 */
class NetworkChangedEvent (@NetState val state:Int) {

    fun isNone(): Boolean {
        return state == NetState.NETWORK_NONE
    }

    fun isUnKnown(): Boolean {
        return state == NetState.NETWORK_UNKNOWN
    }

    fun isMobile(): Boolean {
        return state == NetState.NETWORK_MOBILE
    }

    fun isWIFI(): Boolean {
        return state == NetState.NETWORK_WIFI
    }
}