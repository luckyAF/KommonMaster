package com.luckyaf.kommon.manager.netstate

import android.content.BroadcastReceiver
import android.content.Context
import com.luckyaf.kommon.constant.NetworkType
import android.content.IntentFilter
import com.luckyaf.kommon.manager.netstate.NetStateReceiver.Companion.ANDROID_NET_CHANGE_ACTION
import com.luckyaf.kommon.manager.netstate.NetStateReceiver.Companion.CUSTOM_ANDROID_NET_CHANGE_ACTION
import android.content.Intent

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
@Suppress("unused")
object NetStateManager {
    private var mNetChangeObservers = ArrayList<NetChangeObserver>()
    private var isNetAvailable = false
    private var mBroadcastReceiver:BroadcastReceiver ?=null


    /**
     * 注册
     *
     * @param context context
     */
    fun registerNetworkStateReceiver(context: Context) {
        if (null == mBroadcastReceiver) {
            mBroadcastReceiver = NetStateReceiver.instance
        }
        val filter = IntentFilter()
        filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION)
        filter.addAction(ANDROID_NET_CHANGE_ACTION)
        context.applicationContext.registerReceiver(mBroadcastReceiver, filter)
    }

    /**
     * 清除广播
     *
     * @param context context
     */
    fun unRegisterNetworkStateReceiver(context: Context) {
        if (mBroadcastReceiver != null) {
            try {
                context.applicationContext.unregisterReceiver(mBroadcastReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mNetChangeObservers.clear()
    }


    /**
     * 检查当前网络
     * @param mContext
     */
    fun checkNetworkState(mContext: Context) {
        val intent = Intent()
        intent.action = CUSTOM_ANDROID_NET_CHANGE_ACTION
        mContext.sendBroadcast(intent)
    }



    fun notifyNetworkState(state: NetworkType) {
        mNetChangeObservers.map {
            it.onNetChanged(state)
        }
    }



    /**
     * 添加网络监听
     * 在activity 或fragment中调用
     * @param observer
     */
    fun registerObserver(observer: NetChangeObserver?) {
        observer?:return
        mNetChangeObservers.add(observer)

    }

    /**
     * 移除网络监听
     *  在activity 或fragment中调用
     * @param observer
     */
    fun removeRegisterObserver(observer: NetChangeObserver?) {
        observer?:return
        if (mNetChangeObservers.contains(observer)) {
            mNetChangeObservers.remove(observer)
        }
    }


}