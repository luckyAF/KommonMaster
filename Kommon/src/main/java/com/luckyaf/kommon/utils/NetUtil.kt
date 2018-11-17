package com.luckyaf.kommon.utils

import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Network
import android.content.Context.CONNECTIVITY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.support.annotation.RequiresPermission
import android.net.wifi.WifiInfo
import android.support.v4.content.ContextCompat.getSystemService
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.UnknownHostException


/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/18
 *
 */
object NetUtil {
    /**
     * 没有连接网络
     */
    const val NETWORK_NONE = -1

    /**
     * 网络状态未知
     */
    const val NETWORK_UNKNOWN = 0

    /**
     * 移动网络
     */
    const val NETWORK_MOBILE = 1
    /**
     * 无线网络
     */
    const val NETWORK_WIFI = 2

    /**
     * 获取网络状态
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetWorkState( context: Context): Int {
        val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // 得到连接管理器对象
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE
                }
            } else {
                return NETWORK_NONE
            }
        } else {
            //获取所有网络连接的信息
            val networks = connectivityManager.allNetworks
            //通过循环将网络信息逐个取出来
            for (i in networks.indices) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                val networkInfo = connectivityManager.getNetworkInfo(networks[i])
                if (networkInfo.isConnected) {
                    return if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                        NETWORK_MOBILE
                    } else {
                        NETWORK_WIFI
                    }
                }
            }
        }
        return NETWORK_NONE


    }


    /**
     * check NetworkAvailable
     *
     * @param context
     * @return
     */
    @JvmStatic
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.applicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return !(null == info || !info.isAvailable)

    }

    /**
     * 通过 wifi 获取本地 IP 地址
     *
     * @return IP 地址
     */
    fun getIpAddressByWifi(context: Context): String {
        // 获取wifi服务
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        return intToIp(ipAddress)
    }

    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }

    /**
     * 获取域名 IP 地址
     *
     * @param domain 域名
     * @return IP 地址
     */
    fun getDomainAddress( domain: String): String? {
        val inetAddress: InetAddress
        var result:String? = null
        try {
            inetAddress = InetAddress.getByName(domain)
            result  = inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return result

    }


}