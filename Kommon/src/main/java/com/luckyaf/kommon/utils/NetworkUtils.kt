package com.luckyaf.kommon.utils

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.UnknownHostException
import android.content.Intent
import android.net.NetworkInfo
import android.Manifest.permission.INTERNET
import android.content.Context.WIFI_SERVICE
import android.annotation.SuppressLint
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.telephony.TelephonyManager
import com.luckyaf.kommon.constant.NetworkType
import android.telephony.TelephonyManager.NETWORK_TYPE_IWLAN
import android.telephony.TelephonyManager.NETWORK_TYPE_TD_SCDMA
import android.telephony.TelephonyManager.NETWORK_TYPE_GSM



/**
 * 类描述：
 *
 * 需添加的权限：
 * {@code <uses-permission android:name="android.permission.INTERNET"/>}
 * {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}
 * {@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>}
 * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}
 * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}
 * @author Created by luckyAF on 2018/10/18
 *
 */
@Suppress("unused")

object NetworkUtils {

    /**
     * 获取活动网络信息
     *
     * @return NetworkInfo
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo
    }

    /**
     * 判断网络是否连接
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info?.isConnected?:false
    }

    /**
     * 有时候wifi连接了 用ping的方式可以确定是否可以真的上网
     */
    @RequiresPermission(INTERNET)
    fun isAvailableByPing(): Boolean {
        return isAvailableByPing(null)
    }

    @RequiresPermission(INTERNET)
    fun isAvailableByPing(testIp: String?): Boolean {
        var ip = testIp
        if (ip == null || ip.isEmpty()) {
            ip = "223.5.5.5"// default ping ip
        }
        val result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false)
        return result.result == 0
    }

    /**
     * 判断 wifi 是否打开
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isWifiEnabled(context: Context): Boolean {
        @SuppressLint("WifiManagerLeak")
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        return wifiManager?.isWifiEnabled?:false
    }

    /**
     * 打开或关闭 wifi
     *
     * @param enabled `true`: 打开<br></br>`false`: 关闭
     */
    fun setWifiEnabled(context: Context,enabled: Boolean) {
        @SuppressLint("WifiManagerLeak")
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiManager?: return
        if(enabled != wifiManager.isWifiEnabled){
            wifiManager.isWifiEnabled = enabled
        }
    }

    /**
     * 判断 wifi 是否连接
     *
     * @return `true`: 连接<br></br>`false`: 未连接
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return (cm != null && cm.activeNetworkInfo != null
                && cm.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI)
    }

    /**
     * 判断 wifi 数据是否可用
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    @RequiresPermission(INTERNET)
    fun isWifiAvailable(context: Context): Boolean {
        return isWifiEnabled(context) && isAvailableByPing()
    }

    /**
     * 判断移动数据是否打开
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isMobileDataEnabled(context: Context): Boolean {
        try {
            val tm = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            tm?:return false
            val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
            getMobileDataEnabledMethod?: return getMobileDataEnabledMethod.invoke(tm) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 打开或关闭移动数据
     *
     * @param enabled `true`: 打开<br></br>`false`: 关闭
     */
    fun setMobileDataEnabled(context: Context,enabled: Boolean) {
        try {
            val tm = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            tm?: return
            val setMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType!!)
            setMobileDataEnabledMethod.invoke(tm, enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取网络运营商名称
     *
     * 中国移动、如中国联通、中国电信
     *
     * @return 运营商名称
     */
    fun getNetworkOperatorName(context: Context): String? {
        val tm = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        return tm?.networkOperatorName
    }

    /**
     * 打开无线设置
     */
    fun openWirelessSettings(context: Context) {
        context.startActivity(
                Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }


    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun getNetworkType(context: Context): NetworkType {
        var netType = NetworkType.NETWORK_NO
        val info = getActiveNetworkInfo(context)
        if (info != null && info.isAvailable) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                when (info.subtype) {
                    NETWORK_TYPE_GSM,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN -> netType = NetworkType.NETWORK_2G
                    NETWORK_TYPE_TD_SCDMA,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> netType = NetworkType.NETWORK_3G
                    NETWORK_TYPE_IWLAN,
                    TelephonyManager.NETWORK_TYPE_LTE -> netType = NetworkType.NETWORK_4G
                    else -> {
                        val subtypeName = info.subtypeName
                        //  中国移动 联通 电信 三种 3G 制式
                        if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)) {
                            netType = NetworkType.NETWORK_3G
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN
                        }
                    }
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN
            }
        }
        return netType
    }


    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getWifiEnabled(context: Context): Boolean {
        @SuppressLint("WifiManagerLeak")
        val manager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
        return manager?.isWifiEnabled ?:false
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

    fun getConnectWifiName(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager?.connectionInfo
        return wifiInfo?.ssid
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
    fun getDomainAddress(domain: String): String? {
        val inetAddress: InetAddress
        var result: String? = null
        try {
            inetAddress = InetAddress.getByName(domain)
            result = inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return result

    }


}