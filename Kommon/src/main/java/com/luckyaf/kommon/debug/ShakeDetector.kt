package com.luckyaf.kommon.debug

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.luckyaf.kommon.extension.ERROR

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-19
 *
 */
class ShakeDetector(private val mShakeListener: ShakeListener) : SensorEventListener {

    companion object {
        private const val TAG = "ShakeDetector"
        private const  val SPEED_SHAKE = 4500//这个值越大需要越大的力气来摇晃手机
        private const  val UPDATE_INTERVAL_TIME = 50
    }

    private var mSensorManager: SensorManager? = null
    private var lastX:Float = 0f
    private var lastY:Float = 0f
    private var lastZ:Float = 0f
    private var lastUpdateTime = 0L


    interface ShakeListener {
        fun onShake()
    }

    /**
     * Start listening for shakes.
     */
    fun start(manager: SensorManager?) {
        mSensorManager = manager
        val accelerometer = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            mSensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        } else {
            "错误，当前手机无法获取：ACCELEROMETER传感器，摇一摇功能异常。".ERROR(TAG)
        }
    }

    /**
     * Stop listening for shakes.
     */
    fun stop() {
        mSensorManager?.unregisterListener(this)
        mSensorManager = null

    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
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
                val result =  deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
                 val speed = Math.sqrt(result.toDouble())/ timeInterval * 10000
                 if (speed >= SPEED_SHAKE) {
                     mShakeListener.onShake()
                     }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}




}
