package com.luckyaf.kommonmaster

import com.luckyaf.kommon.Kommon
import com.luckyaf.kommon.base.BaseApp

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/15
 *
 */
class App : BaseApp() {

    override fun initialize() {
        Kommon.init(this,BuildConfig.DEBUG)

    }

    override fun clear() {

    }

}