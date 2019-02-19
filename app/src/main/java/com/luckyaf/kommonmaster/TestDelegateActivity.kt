package com.luckyaf.kommonmaster

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.delegate.extraDelegate
import com.luckyaf.kommon.delegate.preferenceDelegate
import com.luckyaf.kommon.extension.click
import kotlinx.android.synthetic.main.activity_test_delegate.*

class TestDelegateActivity : BaseActivity() {


    private val time: String? by extraDelegate(KEY_TIME, "not init")

    private var lastTime :String ? by preferenceDelegate(KEY_TIME,"not init")

    companion object {

        const val KEY_TIME = "KEY_TIME"

        fun jumpFrom(context: Context, time: String) {
            val intent = Intent(context, TestDelegateActivity::class.java)
            intent.putExtra(KEY_TIME, time)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_test_delegate
    override fun initData(bundle: Bundle?) {
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
     }



    override fun start() {
        txtTime.text = time
        txtLastTime.text = "上次时间 :${lastTime}"
        btnSave.click {
            lastTime = time
        }
        txtLastTime.click {
            var x = 10 / 0
        }
    }

     fun onClick(v: View?) {
    }

}
