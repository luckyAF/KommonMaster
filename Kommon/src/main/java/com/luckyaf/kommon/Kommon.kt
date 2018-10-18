package com.luckyaf.kommon

import android.app.Application
import android.content.Context
import com.luckyaf.kommon.manager.ActivityManager
import kotlin.properties.Delegates

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */

class Kommon private constructor(){

    companion object {
        var context :Context  by Delegates.notNull()
            private set
        var appName:String by Delegates.notNull()
            private set

        fun init(application: Application){
            context = application
            appName = application.packageName
            application.registerActivityLifecycleCallbacks(ActivityManager.instance)
        }
    }
}