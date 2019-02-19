package com.luckyaf.kommon.constant

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
enum class NetworkType {
    // wifi
    NETWORK_WIFI,
    // 4G 网
    NETWORK_4G,
    // 3G 网
    NETWORK_3G,
    // 2G 网
    NETWORK_2G,
    // 未知网络
    NETWORK_UNKNOWN,
    // 没有网络
    NETWORK_NO;

    fun isNone(): Boolean {
        return this == NETWORK_NO
    }
}