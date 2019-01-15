package com.luckyaf.kommon.base

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
 * @author Created by luckyAF on 2018/10/10
 *
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {

    val instance by lazy { this } //这里使用了委托，表示只有使用到instance才会执行该段代码

    /**
     * 网络状态变化的广播
     */

    private var mNetworkChangedReceiver: NetworkChangedReceiver? = null

    /**
     * 提示View
     */
    private var mTipView: View ?= null
    private lateinit var mWindowManager: WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams


    /**
     *  布局
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 初始化页面
     */
    abstract fun initView()

    /**
     * 开始请求操作
     */
    abstract fun start()

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

    open fun doBeforeSetContentView(){

    }

    /**
     * 假如使用了mvp  在次释放presenter
     */
    open fun closeMVP(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        doBeforeSetContentView()
        setContentView(getLayoutId())
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }

        if (enableNetworkTip()) {
            initTipView()
        }
        initView()
        start()

    }

    protected fun initToolbar(toolbar: Toolbar, homeAsUpEnabled: Boolean, title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
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

    override fun onBackPressed() {
        super.onBackPressed()
        // Fragment 逐个出栈
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
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
            closeMVP()
        }
        super.onDestroy()
    }
}