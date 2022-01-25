package com.luckyaf.kommon.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.NonNull
import android.support.annotation.RequiresPermission
import com.luckyaf.kommon.R
import com.luckyaf.kommon.utils.KeyboardUtil

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {
    protected val tag by lazy { this.javaClass.name }
    protected val mContext by lazy { this }
    private val mCompositeDisposable by lazy { CompositeDisposable() }

    private var mContentView: View? = null
    private var savedBundle: Bundle? = null

    companion object {
        private const val KEY_SAVE_BUNDLE = "key_activity_save_bundle"
        private const val VIBRATE_DURATION = 20L
    }

    private var firstStart = true

    /**
     * 获取布局id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData(@NonNull bundle: Bundle)

    /**
     * 初始化数据后 初始化VIew
     */
    abstract fun initView(@NonNull savedInstanceState: Bundle,@NonNull  contentView: View)

    /**
     * 页面展示，可以请求数据了
     */
    abstract fun start()

    /**
     * 是否启用eventBus
     */
    open fun useEventBus() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        savedBundle = if (savedInstanceState == null) {
            intent.extras
        } else {
            savedInstanceState.getBundle(KEY_SAVE_BUNDLE)
        }
        //如果没有任何参数，则初始化 savedBundle，避免调用时 null pointer
        if (savedBundle == null) {
            savedBundle = Bundle()
        }
        initData(savedBundle!!)
        doBeforeSetContentView()
        setRootLayout(getLayoutId())
        doAfterSetContentView()
        initView(savedBundle!!, mContentView!!)
    }




    open fun doBeforeSetContentView() {

    }

    open fun doAfterSetContentView() {

    }

    open fun setRootLayout(layoutId: Int) {
        if (layoutId == -1) {
            return
        }
        mContentView = LayoutInflater.from(this).inflate(layoutId, null)
        setContentView(mContentView)
    }

    override fun onStart() {
        super.onStart()
        if (firstStart) {
            start()
            firstStart = false
        }
    }

    override fun getSelfActivity(): Activity {
        return this
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

    public override fun onDestroy() {
        unSubscribe()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        KeyboardUtil.hideSoftInput(this)
        super.onDestroy()
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(newActivityStartAnim(), nowActivityStopAnim())
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(oldActivityRestartAnim(), nowActivityDestroyAnim())
    }


    open fun nowActivityStopAnim(): Int {
        return R.anim.to_left_out
    }

    open fun newActivityStartAnim(): Int {
        return R.anim.from_right_in
    }

    open fun nowActivityDestroyAnim(): Int {
        return R.anim.to_right_out
    }

    open fun oldActivityRestartAnim(): Int {
        return R.anim.from_left_in
    }


    private fun unSubscribe() {
        mCompositeDisposable.clear()
    }

    protected fun addSubscribe(subscription: Disposable) {
        mCompositeDisposable.add(subscription)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(KEY_SAVE_BUNDLE, this.savedBundle)
        super.onSaveInstanceState(outState)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val app = applicationContext as BaseApp
        if (hasFocus) {
            app.tryReInitialize()
        }
    }

}