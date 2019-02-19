package com.luckyaf.kommon.debug

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.SensorManager
import android.support.v4.app.FragmentActivity
import com.luckyaf.kommon.BuildConfig
import com.luckyaf.kommon.manager.ActivityManager
import java.util.LinkedHashMap

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-19
 *
 */
class ShakeManager private constructor(): ShakeDetector.ShakeListener{

    companion object {
        private val instance =  ShakeManager()
        fun init(context: Context?){
            instance.registerShakeDetector(context)
        }
        fun clear(){
            instance.unregisterShakeDetector()
        }
    }
    private var mShakeDetector: ShakeDetector? = null

    private var mShowing = false
    private var mLastShakeTime: Long = -1
    private val SHAKE_DELAY: Long = 1000

    fun registerShakeDetector(context: Context?) {
        var context = context
        if (!BuildConfig.DEBUG) {
            return
        }
        if (context == null) {
            throw NullPointerException("Context can not be null")
        }
        context = context.applicationContext
        if (mShakeDetector != null) {
            throw RuntimeException("ShakeDetector has been registered")
        }
        mShakeDetector = ShakeDetector(this)
        mShakeDetector?.start(context.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    }

    fun unregisterShakeDetector() {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (mShakeDetector != null) {
            mShakeDetector?.stop()
            mShakeDetector = null
        }
    }

    private fun getTopActivity():Activity?{
        return ActivityManager.instance.getTopActivity()
    }


    override fun onShake() {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (mShowing) {
            return
        }
        val currentTimeMillis = System.currentTimeMillis()
        if (mLastShakeTime != -1L && currentTimeMillis - mLastShakeTime < SHAKE_DELAY) {
            return
        }
        mLastShakeTime = currentTimeMillis
        val activity = getTopActivity()
        if (activity == null) {
            Exception("Shake fail, getActivity is null").printStackTrace()
            return
        }
        val map = LinkedHashMap<String, Any>()
        var currentActivity = activity.javaClass.name
        if (activity is FragmentActivity) {
            val fragments = activity.supportFragmentManager.fragments
            if (fragments != null) {
                currentActivity += "\n"
                for (fragment in fragments) {
                    if (fragment == null) {
                        continue
                    }
                    if (fragment is IRegisterShakeDetector) {
                        (fragment as IRegisterShakeDetector).registerShakeDetector(map)
                    }
                    currentActivity += "\t\t" + fragment.javaClass.simpleName + "\n"
                }
            }
        }
        map["当前Activity"] = currentActivity
        if (map.size < 1) {
            return
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("开发者菜单")
        val array = map.keys.toTypedArray()
        builder.setItems(array) { dialog, which ->
            val obj = map[array[which]]
            if (obj is Class<*>) {
                try {
                    // 检查是否是Activity的子类
                    val clazz = obj as Class<*>?
                    if (Activity::class.java.isAssignableFrom(clazz!!)) {
                        val intent = Intent(activity, clazz)
                        activity.startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            if (obj is Intent) {
                activity.startActivity(obj as Intent?)
            }
            if (obj is DialogInterface.OnClickListener) {
                obj.onClick(dialog, which)
            }
            if (obj is Runnable) {
                obj.run()
            }
            if (obj is String) {
                AlertDialog.Builder(activity)
                        .setTitle(array[which])
                        .setMessage(obj.toString() + "\n")
                        .show()
            }
            if (obj is Array<*> && obj.size == 2) {
                val array = obj as Array<String>?
                AlertDialog.Builder(activity)
                        .setTitle(array!![0])
                        .setMessage(array[1] + "\n")
                        .show()
            }
        }
        val dialog = builder.create()
        mShowing = true
        dialog.setOnDismissListener {
            mShowing = false
        }
        dialog.show()

    }
}