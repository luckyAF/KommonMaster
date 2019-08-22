package com.luckyaf.kommonmaster

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.luckyaf.kommon.extension.clickWithTrigger
import com.luckyaf.kommon.extension.jumpTo
import com.luckyaf.kommon.widget.WebViewActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */
class MainActivity :SmartActivity(){
    override fun getLayoutId() = R.layout.activity_main

    override fun initData(bundle: Bundle) {

    }

    override fun initView(savedInstanceState: Bundle, contentView: View) {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }





        btnNotification.clickWithTrigger {
            jumpTo<TestNotificationActivity>()
        }

        btFinger.clickWithTrigger {
            jumpTo<TestFingerprintActivity>()
        }

        btnNet.clickWithTrigger {
            jumpTo<TestNetActivity>()
        }

        btnWebView.clickWithTrigger {
            WebViewActivity.openUrl(this,"https://www.zhihu.com")
        }


    }

    override fun start() {

    }

}