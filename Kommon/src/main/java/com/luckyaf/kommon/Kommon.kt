package com.luckyaf.kommon

import android.app.Application
import android.content.Context
import com.luckyaf.kommon.manager.ActivityManager
import com.luckyaf.kommon.manager.netstate.NetStateManager
import kotlin.properties.Delegates

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */

object Kommon {

    var context: Context  by Delegates.notNull()
        private set
    var appName: String by Delegates.notNull()
        private set

    fun init(application: Application) {
        context = application
        appName = application.packageName
        NetStateManager.registerNetworkStateReceiver(application)
        application.registerActivityLifecycleCallbacks(ActivityManager.instance)
    }

    fun clear() {
        NetStateManager.unRegisterNetworkStateReceiver(context)
    }


}