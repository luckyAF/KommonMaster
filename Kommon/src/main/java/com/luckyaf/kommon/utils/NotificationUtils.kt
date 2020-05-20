package com.luckyaf.kommon.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import android.text.TextUtils


/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
object NotificationUtils {

    private val ID_HIGH_CHANNEL = "channel_1_oncar"
    private val NAME_HIGH_CHANNEL = "channel_1_name_oncar"

    private val ID_LOW_CHANNEL = "channel_low_onecar"
    private val NAME_LOW_CHANNEL = "channel_name_low_onecar"


    val ID_SHOW_BLOCK_NOTIFICATION = 1001

    private var sNotificationManager: NotificationManager? = null

    /**
     * 文本消息
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param summary       内容
     * @param ticker        出现消息时状态栏的提示文字
     * @param pendingIntent 点击后的intent
     */
    fun setMessageNotification(context: Context, notifyId: Int, smallIconId: Int,largeIcon:Int, title: CharSequence, summary: CharSequence, ticker: CharSequence, pendingIntent: PendingIntent?) {
        val builder
         = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
             NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(smallIconId)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false)// Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        val manager = createNotificationManager(context)
        manager.notify(notifyId, builder.build())
    }
    /**
     * 显示消息中心的消息
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param icon          图标
     * @param summary       内容
     * @param ticker        出现消息时状态栏的提示文字
     * @param pendingIntent 点击后的intent
     */
    fun setInfoNotification(context: Context, notifyId: Int, title: CharSequence,@DrawableRes icon: Int, summary: CharSequence, ticker: CharSequence, pendingIntent: PendingIntent?) {
        val builder
        = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
             NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false)// Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        val manager = createNotificationManager(context)
        manager.notify(notifyId, builder.build())
    }

    /**
     * 设置下载进度通知
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param icon          图标
     * @param ticker        出现消息时状态栏的提示文字
     * @param progress      进度（0-100）
     * @param pendingIntent 点击后的intent
     *
     */
    fun setProgressNotification(context: Context, notifyId: Int, title: CharSequence,@DrawableRes icon:Int, ticker: CharSequence?, progress: Int, pendingIntent: PendingIntent?) {
        val builder =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
             NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, icon))
                .setContentTitle(title)
                .setProgress(100, progress, progress == 0)
                .setOngoing(progress < 100)
                .setAutoCancel(progress == 100)
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        val manager = createNotificationManager(context)
        manager.notify(notifyId, builder.build())
    }


    /**
     * 取消通知
     *
     * @param notifyId 通知ID
     */
    fun cancelNotification(context: Context, notifyId: Int) {
        val manager = createNotificationManager(context)
        manager.cancel(notifyId)
    }

    /**
     * 取消所有通知
     */
    fun cancelNotification(context: Context) {
        val manager = createNotificationManager(context)
        manager.cancelAll()
    }

    private fun createNotificationManager(context: Context): NotificationManager {
        if (sNotificationManager == null) {
            sNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // 适配>=7.0手机通知栏显示问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationHighChannel = NotificationChannel(ID_HIGH_CHANNEL, NAME_HIGH_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                val notificationLowChannel = NotificationChannel(ID_LOW_CHANNEL, NAME_LOW_CHANNEL, NotificationManager.IMPORTANCE_LOW)
                val channelList = ArrayList<NotificationChannel>()
                channelList.add(notificationLowChannel)
                channelList.add(notificationHighChannel)
                sNotificationManager?.createNotificationChannels(channelList)
            }
        }
        return sNotificationManager!!
    }
}