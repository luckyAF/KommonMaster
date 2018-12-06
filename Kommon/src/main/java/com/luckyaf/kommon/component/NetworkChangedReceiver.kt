package com.luckyaf.kommon.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.annotation.RequiresPermission
import com.luckyaf.kommon.event.NetworkChangedEvent
import com.luckyaf.kommon.utils.NetUtil
import org.greenrobot.eventbus.EventBus


/**
 * 类描述：网络广播
 * <p>
 * 需添加的权限
 * {@code  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}
 * @author Created by luckyAF on 2018/10/18
 * </P>
 *
 */
class NetworkChangedReceiver: BroadcastReceiver() {


    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    // <uses-permission android:name="android.permission.INTERNET"/>

//
//    //在onResume()方法注册
//    @Override
//    protected void onResume() {
//        if (netWorkStateReceiver == null) {
//            netWorkStateReceiver = new NetWorkStateReceiver();
//        }
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(netWorkStateReceiver, filter);
//        System.out.println("注册");
//        super.onResume();
//    }
//
//    //onPause()方法注销
//    @Override
//    protected void onPause() {
//        unregisterReceiver(netWorkStateReceiver);
//        System.out.println("注销");
//        super.onPause();
//    }

    fun  registerSelf(context: Context){
        val filter =  IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this,filter)
    }
    fun unregisterSelf(context: Context){
        context.unregisterReceiver(this)
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.action!!, ignoreCase = true)) {
            val netWorkState = NetUtil.getNetWorkState(context)
            EventBus.getDefault().post((NetworkChangedEvent(netWorkState)))
        }
    }
}