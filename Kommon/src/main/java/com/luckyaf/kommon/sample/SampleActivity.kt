package com.luckyaf.kommon.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luckyaf.kommon.R
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.delegate.PreferenceDelegate
import com.luckyaf.kommon.delegate.extraDelegate
import com.luckyaf.kommon.delegate.preferenceDelegate
import com.luckyaf.kommon.extension.put
import kotlinx.android.synthetic.main.layout_recycler_load_more_view.view.*

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
class SampleActivity : BaseActivity(){
    override fun initData(bundle:  Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun showMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        const val KEY_USER = "user"

        fun intent(context: Context, user: SampleData): Intent {
            val intent = Intent(context, SampleActivity::class.java)
            intent.putExtra(KEY_USER, user)
            return intent
        }
    }


    private val thisUser by extraDelegate(KEY_USER)
    private val id :String by preferenceDelegate(KEY_USER,"haha")


    override fun getLayoutId():Int = -1

     fun producePresenter(): SamplePresenter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun jumpToAnother(){

    }

}