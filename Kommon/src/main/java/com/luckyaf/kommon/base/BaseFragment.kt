package com.luckyaf.kommon.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
@Suppress("unused")
abstract class BaseFragment : Fragment() ,IBaseView{

    companion object {
        private const val TAG = "BaseFragment"
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }

    open fun ableLazyLoad():Boolean = false
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

    protected lateinit var mActivity: Activity
    protected lateinit var mInflater: LayoutInflater
    protected lateinit var mContentView: View

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            fragmentManager?.beginTransaction()?.let {
                if (isSupportHidden) {
                    it.hide(this).commitAllowingStateLoss()
                } else {
                    it.show(this).commitAllowingStateLoss()
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mInflater = inflater
        setRootLayout(getLayoutId())
        return mContentView
    }

    override fun setRootLayout(layoutId: Int) {
        if (layoutId == -1){
            return
        }
        mContentView = mInflater.inflate(layoutId, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        initData(bundle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewPrepare = true
        initView(savedInstanceState, mContentView)
        if(!ableLazyLoad()){
            start()
        }else{
            lazyLoadDataIfPrepared()
        }
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




    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            start()
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
     * 页面被杀死后
     */
    override fun onDestroyView() {
        isViewPrepare = false
        hasLoadData = false
        (mContentView.parent as ViewGroup).removeView(mContentView)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }



}