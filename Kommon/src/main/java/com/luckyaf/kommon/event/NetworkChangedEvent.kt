package com.luckyaf.kommon.event

import com.luckyaf.kommon.utils.NetUtil

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/2
 *
 */
class NetworkChangedEvent (val state:Int) {

    fun isNone(): Boolean {
        return state != NetUtil.NETWORK_NONE
    }

    fun isUnKnown(): Boolean {
        return state == NetUtil.NETWORK_UNKNOWN
    }

    fun isMobile(): Boolean {
        return state == NetUtil.NETWORK_MOBILE
    }

    fun isWIFI(): Boolean {
        return state == NetUtil.NETWORK_WIFI
    }
}