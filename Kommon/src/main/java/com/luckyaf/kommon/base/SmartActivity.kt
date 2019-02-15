package com.luckyaf.kommon.base

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.luckyaf.kommon.R
import com.luckyaf.kommon.component.NetworkChangedReceiver
import com.luckyaf.kommon.event.NetworkChangedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-15
 *
 */
abstract class  SmartActivity :BaseActivity(){
    /**
     * 网络状态变化的广播
     */

    private var mNetworkChangedReceiver: NetworkChangedReceiver? = null

    /**
     * 提示View
     */
    private var mTipView: View?= null
    private lateinit var mWindowManager: WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams

    /**
     * 是否使用 EventBus
     */
    open fun useEventBus(): Boolean = true

    /**
     * 是否启用网络提示 TipView
     */
    open fun enableNetworkTip(): Boolean = true

    /**
     * 无网状态—>有网状态 的自动重连操作，子类可重写该方法
     */
    open fun doReConnected() {
        start()
    }


    override fun doAfterSetContentView(){
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        if (enableNetworkTip()) {
            initTipView()
        }
    }

    /**
     * Network Change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: NetworkChangedEvent) {
        checkNetwork(event)
    }


    /**
     * 初始化 TipView
     */
    private fun initTipView() {
        mTipView = layoutInflater.inflate(R.layout.kommon_tip_network_error, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mLayoutParams = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT)
        mLayoutParams.gravity = Gravity.TOP
        mLayoutParams.x = 0
        mLayoutParams.y = 0
        mLayoutParams.windowAnimations = R.style.anim_float_view // add animations
    }

    /**
     * 检查网络状态 确认是否要显示tip
     */
    protected open fun checkNetwork(networkChangedEvent: NetworkChangedEvent) {
        if (enableNetworkTip()) {
            if (networkChangedEvent.isNone()) {
                if (mTipView != null && mTipView?.parent == null) {
                    mWindowManager.addView(mTipView, mLayoutParams)
                }
            } else {
                doReConnected()
                if (mTipView != null && mTipView?.parent != null) {
                    mWindowManager.removeView(mTipView)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 动态注册网络变化广播
        if(enableNetworkTip()){
            mNetworkChangedReceiver = NetworkChangedReceiver()
            mNetworkChangedReceiver?.registerSelf(this)
        }

    }
    override fun onPause() {
        super.onPause()
        if(enableNetworkTip()) {
            mNetworkChangedReceiver?.unregisterSelf(this)
            mNetworkChangedReceiver = null
        }
    }

    override fun onDestroy() {
        if(useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }


}