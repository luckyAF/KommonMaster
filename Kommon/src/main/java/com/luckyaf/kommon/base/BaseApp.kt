package com.luckyaf.kommon.base

import android.app.Application
import android.content.Context
import com.luckyaf.kommon.manager.netstate.NetStateReceiver
import android.content.IntentFilter
import android.os.Build
import com.luckyaf.kommon.Kommon
import com.luckyaf.kommon.manager.netstate.NetStateManager


/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
class BaseApp  : Application() {

    override fun onCreate() {
        super.onCreate()
        Kommon.init(this)
        NetStateManager.registerNetworkStateReceiver(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        NetStateManager.unRegisterNetworkStateReceiver(this)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        NetStateManager.unRegisterNetworkStateReceiver(this)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

}