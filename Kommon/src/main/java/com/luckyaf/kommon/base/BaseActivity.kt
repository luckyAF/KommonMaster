package com.luckyaf.kommon.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() ,IBaseView{

    val instance by lazy { this } //这里使用了委托，表示只有使用到instance才会执行该段代码
    private lateinit var mContentView: View

    open fun doBeforeSetContentView(){

    }
    open fun doAfterSetContentView(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        doBeforeSetContentView()
        setRootLayout(getLayoutId())
        doAfterSetContentView()
        initView(savedInstanceState,mContentView)
        start()

    }

    override fun setRootLayout(layoutId: Int) {
        if (layoutId == -1){
            return
        }
        mContentView = LayoutInflater.from(this).inflate(layoutId, null)
        setContentView(mContentView)
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