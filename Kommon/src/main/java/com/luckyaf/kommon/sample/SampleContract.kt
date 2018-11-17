package com.luckyaf.kommon.sample

import com.luckyaf.kommon.base.BasePresenter
import com.luckyaf.kommon.base.BaseView

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/31
 *
 */
interface SampleContract {
    interface View : BaseView<Presenter> {
        fun showMessage()
    }
    interface Presenter : BasePresenter{
        fun getData()
    }
}