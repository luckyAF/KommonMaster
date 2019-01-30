package com.luckyaf.kommonmaster

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout

import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.extension.aboveApi
import com.luckyaf.kommon.extension.clickWithTrigger
import com.luckyaf.kommon.widget.popup.SmartPopup
import com.luckyaf.kommon.widget.popup.XGravity
import com.luckyaf.kommon.widget.popup.YGravity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import com.luckyaf.kommon.extension.jumpTo


class MainActivity : BaseActivity() {


    override fun getLayoutId(): Int = R.layout.activity_main



    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        btnDelegate.clickWithTrigger {
            TestDelegateActivity.jumpFrom(instance,Date().toString())
        }

        btnPopDown.clickWithTrigger {
            SmartPopup.create(this)
                    .setContentView(R.layout.pop_test)
                    .apply()
                    .showAsDropDown(it,YGravity.BELOW,XGravity.CENTER,0)
        }

        btnPopCenter.clickWithTrigger {
            SmartPopup.create(this)
                    .setContentView(R.layout.pop_test)
                    .apply()
                    .showAtLocation(it,Gravity.CENTER,0,0)
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

        btnRecycler.clickWithTrigger {
            jumpTo<TestRecyclerActivity>()
        }



      }



    override fun start() {
        aboveApi(Build.VERSION_CODES.O){

        }
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
