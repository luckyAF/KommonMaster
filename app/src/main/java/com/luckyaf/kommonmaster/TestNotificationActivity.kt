package com.luckyaf.kommonmaster

import android.annotation.TargetApi
import android.app.Notification.FLAG_ONGOING_EVENT
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews
import com.luckyaf.kommon.extension.clickWithTrigger
import kotlinx.android.synthetic.main.activity_test_notification.*


/**
 * 类描述：通知测试  适配 8.0
 * @author Created by luckyAF on 2018/11/28
 *
 */
class TestNotificationActivity : SmartActivity() {


    private var mContentViewBig: RemoteViews? = null
    private var mContentViewSmall:RemoteViews? = null

    override fun getLayoutId() = R.layout.activity_test_notification

    override fun initData(bundle: Bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "chat"
            var channelName = "聊天消息"
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
            channelId = "subscribe"
            channelName = "订阅消息"
            importance = NotificationManager.IMPORTANCE_DEFAULT
            createNotificationChannel(channelId, channelName, importance)
            channelId = "music"
            channelName = "播放音乐"
            importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }

    }

    override fun initView(savedInstanceState: Bundle, contentView: View) {
        btnMessage.clickWithTrigger {
            sendChatMsg()
        }
        btnSubscribe.clickWithTrigger {
            sendSubscribeMsg()
        }
        btnMedia.clickWithTrigger {
            showMedia()
        }
    }

    override fun start() {
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(
                NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendChatMsg() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        val notification = NotificationCompat.Builder(this, "chat")
                .setFullScreenIntent(contentIntent,true)
                .setContentTitle("收到一条聊天消息")
                .setContentText("今天中午吃什么？")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build()
        manager.notify(1, notification)
    }

    fun sendSubscribeMsg() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        val notification = NotificationCompat.Builder(this, "subscribe")
                .setContentIntent(contentIntent)
                .setContentTitle("收到一条订阅消息")
                .setContentText("地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .build()
        manager.notify(2, notification)
    }

    /**
     * Show a notification while this service is running.
     */
    private fun showMedia() {

//        val channel =  NotificationChannel("1",
//                "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
//        channel.enableLights(true);
//        channel.setLightColor(Color.GREEN); //小红点颜色
//        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//        notificationManager.createNotificationChannel(channel)


        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Set the info for the views that show in the notification panel.
        val notification = NotificationCompat.Builder(this,"music")
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build()
        notification.flags = FLAG_ONGOING_EVENT


        // Send the notification.
        manager.notify(3,notification)
    }

    fun getSmallContentView(): RemoteViews?{
        if (mContentViewSmall == null) {
            mContentViewSmall = RemoteViews(packageName, R.layout.remote_test_notification_small)

        }
        return mContentViewSmall
    }

    fun getBigContentView(): RemoteViews?{
        if (mContentViewBig == null) {
            mContentViewBig = RemoteViews(packageName, R.layout.remote_test_notification_big)

        }
        return mContentViewBig
    }


}