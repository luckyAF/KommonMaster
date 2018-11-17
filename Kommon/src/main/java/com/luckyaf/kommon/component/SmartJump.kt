package com.luckyaf.kommon.component

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.SparseArray
import com.luckyaf.kommon.BuildConfig

/**
 * 类描述：跳转
 * @author Created by luckyAF on 2018/11/5
 *
 */
@Suppress("unused")
class SmartJump private constructor(fragmentManager: FragmentManager) {

    companion object {

        private val TAG = BuildConfig.APPLICATION_ID + SmartJump::class.java.simpleName

        fun from(activity: FragmentActivity): SmartJump {
            return SmartJump(activity.supportFragmentManager)
        }

        fun from(fragment: Fragment): SmartJump {
            return SmartJump(fragment.childFragmentManager)
        }

        fun with(fragmentManager: FragmentManager): SmartJump {
            return SmartJump(fragmentManager)
        }
    }

    private val resultBridgeFragment: ResultBridgeFragment


    fun startForResult(intent: Intent, callback: Callback) {
        resultBridgeFragment.startForResult(intent, callback)
    }

    fun startForResult(clazz: Class<*>, callback: Callback) {
        val intent = Intent(resultBridgeFragment.activity, clazz)
        startForResult(intent, callback)
    }

    interface Callback {
        /**
         * 回调
         * @param resultCode  code
         * @param data   data
         */
        fun onActivityResult(resultCode: Int, data: Intent?)
    }


    init {
        resultBridgeFragment = getResultBridgeFragment(fragmentManager)
    }

    private fun getResultBridgeFragment(fragmentManager: FragmentManager): ResultBridgeFragment {
        var bridgeFragment = fragmentManager.findFragmentByTag(TAG) as ResultBridgeFragment?
        if (bridgeFragment == null) {
            bridgeFragment = ResultBridgeFragment()
            fragmentManager
                    .beginTransaction()
                    .add(bridgeFragment, TAG)
                    .commitNow()
        }
        return bridgeFragment
    }

    private fun findFragment(fragmentManager: FragmentManager): ResultBridgeFragment {
        return fragmentManager.findFragmentByTag(TAG) as ResultBridgeFragment
    }

    class ResultBridgeFragment : Fragment() {
        private val mCallbacks = SparseArray<Callback>()
        /**
         * 每次启动都会有个不同的requestCode
         */
        private var uniqueCode = 1

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }


        /**
         * 防止 同时多个activity启动 造成request相同
         * @param intent intent
         * @param callback 回调
         */
        @Synchronized
        fun startForResult(intent: Intent, callback: SmartJump.Callback) {
            uniqueCode++
            mCallbacks.put(uniqueCode, callback)
            startActivityForResult(intent, uniqueCode)
            // 保证requestCode 每个都不同
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            val callback = mCallbacks.get(requestCode)
            callback?.onActivityResult(resultCode, data)
            mCallbacks.remove(requestCode)
        }
    }


}