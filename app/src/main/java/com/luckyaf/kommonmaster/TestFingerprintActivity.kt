package com.luckyaf.kommonmaster

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.View
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.extension.toastShort

/**
 * 类描述：指纹
 * @author Created by luckyAF on 2018/11/28
 *
 */
class TestFingerprintActivity: BaseActivity() {

    private val TAG = "gryphon"
    private lateinit var  mBiometricPrompt: BiometricPrompt
    private lateinit var mCancellationSignal : CancellationSignal
    private lateinit var mAuthenticationCallback : BiometricPrompt.AuthenticationCallback


    override fun getLayoutId() = R.layout.activity_test_fingerprint

    override fun initData(bundle: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mBiometricPrompt = BiometricPrompt.Builder(this)
                    .setTitle("指纹验证")
                    .setDescription("描述")
                    .setNegativeButton("取消", mainExecutor, object :DialogInterface.OnClickListener {

                         override fun onClick(dialogInterface:DialogInterface , i:Int ) {
                            Log.i(TAG, "Cancel button clicked")
                        }
                    })
                    .build()
            mCancellationSignal =  CancellationSignal()
            mCancellationSignal.setOnCancelListener(object: CancellationSignal.OnCancelListener {
                override fun onCancel() {
                    //handle cancel result
                    Log.i(TAG, "Canceled")
                }
            })
            mAuthenticationCallback = object :BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode:Int , errString:CharSequence ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.i(TAG, "onAuthenticationError " + errString)
                }

                override fun onAuthenticationSucceeded(result:BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.i(TAG, "onAuthenticationSucceeded " + result.toString())
                }
                override
                fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.i(TAG, "onAuthenticationFailed ")
                }
            }
            mBiometricPrompt.authenticate(mCancellationSignal, mainExecutor, mAuthenticationCallback);
        }else{
            toastShort("不是android P")
        }
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun start() {
    }
}