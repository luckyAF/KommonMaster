package com.luckyaf.kommon.base



import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDex



/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
open class BaseApp : Application() {
    private var hasInitialize = false
    override fun onCreate() {
        super.onCreate()
        hasInitialize = true
        disableAPIDialog()
        initialize()
    }

    open fun initialize(){
    }
    open fun clear(){
    }


    internal fun tryReInitialize() {
        if (!hasInitialize) {
            initialize()
        }
    }


    /**
     * 反射 9.0 禁止弹窗
     */
    @TargetApi(Build.VERSION_CODES.P)
    private fun disableAPIDialog() {
        if (Build.VERSION.SDK_INT < 28) {
            return
        }
        try {
            val aClass = Class.forName("android.content.pm.PackageParser\$Package")
            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
            declaredConstructor.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val clazz = Class.forName("android.app.ActivityThread")
            val currentActivityThread = clazz.getDeclaredMethod("currentActivityThread")
            currentActivityThread.isAccessible = true
            val activityThread = currentActivityThread.invoke(null)
            val mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(base)
    }

    override fun onTerminate() {
        super.onTerminate()
        clear()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        clear()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

}