package com.luckyaf.kommon.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus


/**
 * 类描述：基类Fragment
 *
 * @author Created by luckyAF on 2019-07-16
 */
abstract class BaseFragment: androidx.fragment.app.Fragment(),BaseView{
    protected val LOG_TAG by lazy { this.javaClass.name }
    protected lateinit var mContext  :Context
    protected lateinit var mActivity:Activity
    private val mCompositeDisposable by lazy { CompositeDisposable() }
    private var mContentView: View? = null
    private var savedBundle: Bundle? = null
    private var mInflater: LayoutInflater?=null


    companion object {
        private const val KEY_SAVE_BUNDLE = "key_activity_save_bundle"
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }


    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false

    /**
     * fragment是否已经在前台
     */
    private var isFront = false
    /**
     * activity 是否在后台
     */
    private var isActivityBackground = false

    /**
     * 是否启用eventBus
     */
    open fun useEventBus() = false

    open fun ableLazyLoad() =true

    /**
     * 获取布局id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData(bundle: Bundle)

    /**
     * 初始化数据后 初始化VIew
     */
    abstract fun initView(savedInstanceState: Bundle, contentView: View)

    /**
     * 页面展示，可以请求数据了
     */
    abstract fun start()




    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            savedBundle = savedInstanceState.getBundle(KEY_SAVE_BUNDLE)
            val manager = fragmentManager
            if (null != manager) {
                val transaction = manager.beginTransaction()
                if (isSupportHidden) {
                    transaction.hide(this).commitAllowingStateLoss()
                } else {
                    transaction.show(this).commitAllowingStateLoss()
                }
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mInflater = inflater
        setRootLayout(getLayoutId())
        return mContentView
    }

    private fun setRootLayout(layoutId: Int) {
        if (layoutId == -1) {
            return
        }
        if (null == mContentView) {
            mContentView = mInflater?.inflate(layoutId, null)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //如果没有任何参数，则初始化 savedBundle，避免调用时 null pointer
        savedBundle = arguments
        if (savedBundle == null) {
            savedBundle = Bundle()
        }
        initData(savedBundle!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewPrepare = true
        if (useEventBus()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        }
        initView(savedBundle!!, mContentView!!)
    }

    override fun onStart() {
        super.onStart()
        if (!ableLazyLoad() && !hasLoadData) {
            start()
        } else {
            lazyLoadDataIfPrepared()
        }
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (ableLazyLoad()) {
                lazyLoadDataIfPrepared()
            }
            isFront = true
            onTrueResume()
        } else {
            if (isFront) {
                isFront = false
                onTruePause()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (isActivityBackground && userVisibleHint) {
            isFront = true
            onTrueResume()
        }
        isActivityBackground = false
    }

    override fun onPause() {
        super.onPause()
        isFront = false
        onTruePause()
        isActivityBackground = true
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            start()
            hasLoadData = true
        }
    }

    open fun onTrueResume() {

    }

    open fun onTruePause() {

    }

    /**
     * 页面被杀死后
     */
    override fun onDestroyView() {
        isViewPrepare = false
        hasLoadData = false
        (mContentView?.parent as ViewGroup?)?.removeView(mContentView)
        super.onDestroyView()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(KEY_SAVE_BUNDLE, this.savedBundle)
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
        super.onSaveInstanceState(outState)
    }


    override fun getSelfActivity() = mActivity


}