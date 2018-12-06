package com.luckyaf.kommon.constants

import android.support.annotation.IntDef

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/27
 *
 */
@IntDef(NetState.NETWORK_NONE, NetState.NETWORK_UNKNOWN,NetState.NETWORK_MOBILE,NetState.NETWORK_WIFI)
@Retention(AnnotationRetention.SOURCE)
annotation class NetState {
    companion object {
        /**
         * 没有连接网络
         */
        const val NETWORK_NONE = -1

        /**
         * 网络状态未知
         */
        const val NETWORK_UNKNOWN = 0

        /**
         * 移动网络
         */
        const val NETWORK_MOBILE = 1
        /**
         * 无线网络
         */
        const val NETWORK_WIFI = 2
    }
}