package com.luckyaf.kommon.manager.crash

import android.content.Context
import com.luckyaf.kommon.manager.ActivityManager
import com.luckyaf.kommon.utils.LogUtil
import android.os.Looper
import android.support.v7.app.AlertDialog


/**
 * 类描述：
 * 方案概述
APP在后台时，不提示Crash；
APP刚启动时，因为无法启动Crash处理的Activity，就调用默认的CrashHandler；
其他情况下，弹出不可取消的对话框，用户可选择退出应用或者重启应用（忽略Crash会导致不可知的程序行为，所以没有忽略的选项，只是让用户自主退出，提升用户体验）

 * @author Created by luckyAF on 2018/10/15
 *
 */
class CrashHandler {

    private  var crashListener: CrashListener

    companion object {

        fun init() = CrashHandler()
    }

    init {
        crashListener = DefaultCrashListener()
        start()
    }

    fun setCrashListener(crashListener: CrashListener) {
        this.crashListener = crashListener
    }

    private fun start() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            exception?.printStackTrace()
            //程序是否在后台
            val isBackground = ActivityManager.instance.isBackground()
             //APP初始化中，还未启动任何Activity，此时无法启动Crash处理的Activity
            if (ActivityManager.instance.activityCount() == 0) {
                defaultHandler?.uncaughtException(thread, exception)
            } else if (isBackground) {
                // 在后台就退出应用
                Runtime.getRuntime().exit(1)
            } else {
                object : Thread() {
                    override fun run() {
                        // 在当前线程创建消息队列(对话框的显示需要消息队列)
                        Looper.prepare()
                        showCrashDialog(ActivityManager.instance.getNowContext())
                        // 启动消息队列(在队列推出前,后面的代码不会被执行,在这里,后面没有代码了.)
                        Looper.loop()
                    }
                }.start()


            }


        }
    }



    private fun showCrashDialog(context: Context) {
        LogUtil.e("CrashHandler", context.packageName)
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("通知")
                .setMessage("很抱歉,程序出现异常")
                .setNegativeButton("退出应用") { _,_ ->
                    ActivityManager.instance.exitApp()
                }
                .setPositiveButton("重启应用") { _ ,_->
                    ActivityManager.instance.restartApp()

                }
                .show()

    }

}