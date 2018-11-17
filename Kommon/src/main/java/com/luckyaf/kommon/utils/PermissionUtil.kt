package com.luckyaf.kommon.utils

import android.Manifest
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observer
import io.reactivex.disposables.Disposable



/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/18
 *
 */
@Suppress("unused")
object PermissionUtil {

    interface RequestPermission {
        /**
         * 权限请求成功
         */
        fun onRequestPermissionSuccess()

        /**
         * 用户拒绝了权限请求, 权限请求失败, 但还可以继续请求该权限
         *
         * @param permissions 请求失败的权限名
         */
        fun onRequestPermissionFailure(permissions: List<String>)

        /**
         * 用户拒绝了权限请求并且用户选择了以后不再询问, 权限请求失败, 这时将不能继续请求该权限, 需要提示用户进入设置页面打开该权限
         *
         * @param permissions 请求失败的权限名
         */
        fun onRequestPermissionFailureWithAskNeverAgain(permissions: List<String>)
    }

    fun requestPermission(requestPermission: RequestPermission, rxPermissions: RxPermissions, vararg permissions: String) {
        if ( permissions.isEmpty()) {
            return
        }
        val needRequest = ArrayList<String>()
        //过滤调已经申请过的权限
        for (permission in permissions) {
            if (!rxPermissions.isGranted(permission)) {
                needRequest.add(permission)
            }
        }

        //全部权限都已经申请过，直接执行操作
        if (needRequest.isEmpty()) {
            requestPermission.onRequestPermissionSuccess()
        } else {//没有申请过,则开始申请
            rxPermissions
                    .requestEach(*needRequest.toTypedArray())
                    .buffer(permissions.size)
                    .subscribe(object :Observer<List<Permission>>{
                        override fun onComplete() {
                        }
                        override fun onSubscribe(d: Disposable) {
                        }
                        override fun onNext(permissions: List<Permission>) {
                            val failurePermissions = ArrayList<String>()
                            val askNeverAgainPermissions = ArrayList<String>()
                            permissions.forEach {
                                if (!it.granted) {
                                    if (it.shouldShowRequestPermissionRationale) {
                                        failurePermissions.add(it.name)
                                    } else {
                                        askNeverAgainPermissions.add(it.name)
                                    }
                                }
                            }

                            if (failurePermissions.size > 0) {
                                requestPermission.onRequestPermissionFailure(failurePermissions)
                            }

                            if (askNeverAgainPermissions.size > 0) {
                                requestPermission.onRequestPermissionFailureWithAskNeverAgain(askNeverAgainPermissions)
                            }

                            if (failurePermissions.size == 0 && askNeverAgainPermissions.size == 0) {
                                requestPermission.onRequestPermissionSuccess()
                            }
                        }

                        override fun onError(e: Throwable) {
                        }

                    })

        }

    }


    /**
     * 请求摄像头权限
     */
    fun launchCamera(requestPermission: RequestPermission, rxPermissions: RxPermissions) {
        requestPermission(requestPermission, rxPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    }


    /**
     * 请求外部存储的权限
     */
    fun externalStorage(requestPermission: RequestPermission, rxPermissions: RxPermissions) {
        requestPermission(requestPermission, rxPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    /**
     * 请求发送短信权限
     */
    fun sendSms(requestPermission: RequestPermission, rxPermissions: RxPermissions) {
        requestPermission(requestPermission, rxPermissions, Manifest.permission.SEND_SMS)
    }


    /**
     * 请求打电话权限
     */
    fun callPhone(requestPermission: RequestPermission, rxPermissions: RxPermissions) {
        requestPermission(requestPermission, rxPermissions, Manifest.permission.CALL_PHONE)
    }


    /**
     * 请求获取手机状态的权限
     */
    fun readPhoneState(requestPermission: RequestPermission, rxPermissions: RxPermissions) {
        requestPermission(requestPermission, rxPermissions, Manifest.permission.READ_PHONE_STATE)
    }
}