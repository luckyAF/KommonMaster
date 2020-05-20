package com.luckyaf.kommon.extension

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.Vibrator
import androidx.annotation.RequiresApi
import android.telephony.TelephonyManager
import android.view.inputmethod.InputMethodManager

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/3
 *
 */

inline val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

inline val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

inline val Context.telephonyManager: TelephonyManager
    get() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

inline val Context.activityManager: ActivityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

inline val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

inline val Context.appWidgetManager:AppWidgetManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager

inline val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

inline val Context.clipboardManager:ClipboardManager
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

inline val Context.bluetoothManager: BluetoothManager
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    get() = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

inline val Context.audioManager:AudioManager
    get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

inline val Context.batteryManager:BatteryManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = getSystemService(Context.BATTERY_SERVICE) as BatteryManager

inline val Context.cameraManager:CameraManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = getSystemService(Context.CAMERA_SERVICE) as CameraManager

inline val Context.vibrator:Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator