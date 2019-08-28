package com.luckyaf.kommon.manager.shake

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.luckyaf.kommon.utils.Logger
import kotlin.math.sqrt

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-28
 *
 */
class ShakeDetector(private val mShakeListener: () -> Unit) : SensorEventListener {

    /**
     * 这个值越大需要越大的力气来摇晃手机
     */
    private val SPEED_SHAKE = 4500
    private val UPDATE_INTERVAL_TIME = 50


    private var mSensorManager: SensorManager? = null
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var lastUpdateTime: Long = 0


    fun start(manager: SensorManager) {
        mSensorManager = manager
        val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            mSensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        } else {
            Logger.e("错误，当前手机无法获取：ACCELEROMETER传感器，摇一摇功能异常。")
        }
    }

    /**
     * Stop listening for shakes.
     */
    fun stop() {
        mSensorManager?.unregisterListener(this)
        mSensorManager = null

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        val currentUpdateTime = System.currentTimeMillis()
        val timeInterval = currentUpdateTime - lastUpdateTime
        if (timeInterval < UPDATE_INTERVAL_TIME) {
            return
        }
        lastUpdateTime = currentUpdateTime
        val x = sensorEvent.values[0]
        val y = sensorEvent.values[1]
        val z = sensorEvent.values[2]

        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ

        lastX = x
        lastY = y
        lastZ = z
        val result = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
        val speed = (sqrt(result.toDouble()) / timeInterval * 10000).toInt()
        if (speed >= SPEED_SHAKE) {
            mShakeListener.invoke()
        }
    }
}