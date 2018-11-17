package com.luckyaf.kommonmaster

import android.app.Application
import com.luckyaf.kommon.Kommon
import com.luckyaf.kommon.manager.crash.CrashHandler

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/15
 *
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Kommon.init(this)
        CrashHandler.init()
    }
}