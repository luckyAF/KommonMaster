package com.luckyaf.kommon.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
interface IBaseView {
    @LayoutRes fun  getLayoutId(): Int
    fun setRootLayout(@LayoutRes layoutId: Int = -1)
    fun initData(bundle: Bundle?)
    fun initView(savedInstanceState: Bundle?, contentView: View)
    fun start()
}