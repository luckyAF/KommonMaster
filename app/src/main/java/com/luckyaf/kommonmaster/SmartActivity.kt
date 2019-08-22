package com.luckyaf.kommonmaster

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.constant.NetworkType
import org.greenrobot.eventbus.EventBus
import com.luckyaf.kommon.manager.netstate.NetChangeObserver
import com.luckyaf.kommon.manager.netstate.NetStateManager
import com.luckyaf.kommon.utils.ToastUtil


/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-15
 *
 */
abstract class  SmartActivity : BaseActivity(){


    /**
     * 网络观察者
     */
    private var mNetChangeObserver: NetChangeObserver? = null

    /**
     * 提示View
     */
    private var mTipView: View?= null
    private lateinit var mWindowManager: WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams
    private var tipShowing = false


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


    override fun doBeforeSetContentView(){
        if (enableNetworkTip()) {
            initTipView()
            mNetChangeObserver = object :NetChangeObserver{
                override fun onNetChanged(state: NetworkType) {
                    checkNetwork(state)
                }
            }
        }
    }




    /**
     * 初始化 TipView
     */
    private fun initTipView() {
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mTipView = layoutInflater.inflate(R.layout.kommon_tip_network_error, null)
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
    protected open fun checkNetwork(netState: NetworkType) {
        if (enableNetworkTip()) {
            if (netState.isNone()) {
                if (mTipView != null && mTipView?.parent == null) {
                    mWindowManager.addView(mTipView, mLayoutParams)
                    tipShowing = true
                }
            } else {
                doReConnected()
                if (mTipView != null && mTipView?.parent != null) {
                    mWindowManager.removeView(mTipView)
                    tipShowing = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 动态注册网络变化广播
        if(enableNetworkTip()){
            NetStateManager.registerObserver(mNetChangeObserver)
        }

    }
    override fun onPause() {
        super.onPause()
        if(enableNetworkTip()) {
            NetStateManager.registerObserver(mNetChangeObserver)
        }
    }

    override fun showMessage(message: String, messageType: Int) {
        ToastUtil.show(mContext,message)
    }

    override fun showLoading(message: String) {
    }

    override fun hideLoading() {
    }


    override fun onDestroy() {
        if(tipShowing){
            mWindowManager.removeViewImmediate(mTipView)
        }
        if(useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

}