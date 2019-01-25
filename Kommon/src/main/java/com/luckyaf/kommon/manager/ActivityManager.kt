package com.luckyaf.kommon.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.luckyaf.kommon.Kommon
import com.luckyaf.kommon.utils.ToastUtil
import java.lang.ref.WeakReference
import java.util.*

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/16
 *
 */
class ActivityManager:Application.ActivityLifecycleCallbacks {

    private  var mContext:Context?=null
    private val activityStack = Stack<WeakReference<Activity>>()
    private var lastTryExit:Long = 0
    private var activityForGroundCount:Int = 0



    companion object {
        val instance: ActivityManager by lazy { ActivityManager() }
    }




    /**
     * 获取当前Activity的context
     */
    fun getNowContext(): Context {
        return this.mContext ?: Kommon.context
    }

    fun activityCount() : Int{
        return activityStack.size
    }

    /**
     * 应用是否在后台
     */
    fun isBackground():Boolean{
        return activityForGroundCount == 0
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        updateContext(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        updateContext(activity)
        activityForGroundCount ++

    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityForGroundCount --
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        updateContext(activity)
        pushActivity(activity)

    }



    private fun updateContext(activity: Activity) {
        mContext = activity.parent ?: activity
    }



    // 将当前Activity推入栈中
    fun pushActivity(activity: Activity) {
        activityStack.add(WeakReference(activity))
    }

    /**
     * 结束指定的 Activity
     * @param activity Activity
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            activityStack.forEach {
                if (it.get() == activity){
                    activityStack.remove(it)
                    return
                }
            }
        }


    }

    // 退出栈中所有Activity
    fun clearAllActivity() {
        while (!activityStack.isEmpty()) {
            val activity = activityStack.pop()?.get()
            activity?.finish()
        }
    }

    fun exitApp(){
        clearAllActivity()
        mContext = null
        System.exit(0)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun tryExit(){
        if (System.currentTimeMillis() - lastTryExit > 2000 ) {
            ToastUtil.show(mContext, "再次点击退出程序")
            lastTryExit = System.currentTimeMillis()
        } else {
            exitApp()
        }
    }

    fun restartApp(){
        KillSelfService.restart(getNowContext(),1000)
    }


}