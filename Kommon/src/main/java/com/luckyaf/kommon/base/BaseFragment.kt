package com.luckyaf.kommon.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
abstract class BaseFragment : Fragment() {

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), null)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
            onTrueResume()
        } else {
            if (isFront) {
                onTruePause()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (isActivityBackground && userVisibleHint) {
            onTrueResume()
        }
        isActivityBackground = false

    }

    override fun onPause() {
        super.onPause()
        onTruePause()
        isActivityBackground = true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        isViewPrepare = true
        initView()
        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }


    open fun onTrueResume() {
        isFront = true
    }

    open fun onTruePause() {
        isFront = false
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()


    /**
     * 是否使用 EventBus
     */
    open fun useEventBus(): Boolean = false

    abstract fun initData()


    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    /**
     * 页面被杀死后
     */
    override fun onDestroyView() {
        super.onDestroyView()
        isViewPrepare = false
        hasLoadData = false
    }

    /**
     * 假如使用了mvp  在次释放presenter
     */
    open fun closeMVP(){

    }

    override fun onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        closeMVP()
        super.onDestroy()
    }
}