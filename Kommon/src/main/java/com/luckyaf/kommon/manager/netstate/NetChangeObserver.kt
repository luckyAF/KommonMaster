package com.luckyaf.kommon.manager.netstate

import com.luckyaf.kommon.constant.NetworkType

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
interface NetChangeObserver {
    fun onNetChanged(state: NetworkType)
}