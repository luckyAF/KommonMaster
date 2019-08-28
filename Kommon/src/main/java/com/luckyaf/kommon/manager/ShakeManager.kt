package com.luckyaf.kommon.manager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.hardware.SensorManager
import com.luckyaf.kommon.BuildConfig
import com.luckyaf.kommon.manager.shake.ShakeDetector
import com.luckyaf.kommon.mvi.IIntent
import com.luckyaf.kommon.mvi.IntentView

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-28
 *
 */
object ShakeManager {
    private var mShakeDetector: ShakeDetector? = null
    private var mShowing = false
    private var mLastShakeTime: Long = -1
    private const val SHAKE_DELAY = 1000L

    fun init(context: Context) {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (mShakeDetector != null) {
            throw RuntimeException("ShakeDetector has been registered")
        }
        mShakeDetector = ShakeDetector {
            shake()
        }
        mShakeDetector?.start(context.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    }

    fun clear() {
        if (!BuildConfig.DEBUG) {
            return
        }
        mShakeDetector?.stop()
        mShakeDetector = null

    }


    private fun shake() {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (mShowing) {
            return
        }
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - mLastShakeTime < SHAKE_DELAY) {
            return
        }
        mLastShakeTime = currentTimeMillis
        val activity = ActivityManager.instance.getTopActivity()

        if (activity is IntentView) {
            showDialog(activity, activity, activity.provideIntents())
        }
    }

    private fun showDialog(activity: Activity, intentView: IntentView, map: Map<String, IIntent>) {
        val builder = AlertDialog.Builder(activity)
        val array = map.keys.toTypedArray()
        builder.setTitle("开发者菜单")
                .setItems(array) { _, which ->
                    val intent = map[array[which]]
                    intent?.let {
                        intentView.processor(it)
                    }
                }
        val dialog = builder.create()
        mShowing = true
        dialog.setOnDismissListener { mShowing = false }
        dialog.show()
    }


}