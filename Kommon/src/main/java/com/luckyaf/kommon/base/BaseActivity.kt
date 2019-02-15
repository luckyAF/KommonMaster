package com.luckyaf.kommon.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {

    val instance by lazy { this } //这里使用了委托，表示只有使用到instance才会执行该段代码


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



    open fun doBeforeSetContentView(){

    }
    open fun doAfterSetContentView(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        doBeforeSetContentView()
        setContentView(getLayoutId())
        doAfterSetContentView()
        initView()
        start()

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


}