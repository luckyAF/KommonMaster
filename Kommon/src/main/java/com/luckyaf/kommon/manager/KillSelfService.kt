package com.luckyaf.kommon.manager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder



/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/17
 *
 */
class KillSelfService :Service(){


    companion object {
        private const val PACKAGE_NAME:String = "PackageName"
        private const val RESTART_DELAYED:String="RestartDelayed"
        fun restart(context: Context,delayed:Long = 2000){
            /**开启一个新的服务,用来重启本APP*/
            val intent1 = Intent(context, KillSelfService::class.java)
            intent1.putExtra(PACKAGE_NAME, context.packageName)
            intent1.putExtra(RESTART_DELAYED, delayed)
            context.startService(intent1)
            /**杀死整个进程**/
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }


    var handler: Handler
        private set


    init {
        handler = Handler()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val stopDelayed = intent.getLongExtra(RESTART_DELAYED, 2000)
        val packageName = intent.getStringExtra(PACKAGE_NAME)
        handler.postDelayed({
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(launchIntent)
            this@KillSelfService.stopSelf()
        }, stopDelayed)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}