package com.luckyaf.kommon.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener{

    val instance by lazy { this } //这里使用了委托，表示只有使用到instance才会执行该段代码

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        initData()
        start()

    }

    abstract fun getLayoutId() : Int
    abstract fun initView()
    abstract fun initData()
    abstract fun start()
}