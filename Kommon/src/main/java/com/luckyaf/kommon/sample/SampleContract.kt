package com.luckyaf.kommon.sample

import com.luckyaf.kommon.base.IPresenter
import com.luckyaf.kommon.base.IView

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/31
 *
 */
interface SampleContract {
    interface View : IView {
        fun showMessage()
    }
    interface Presenter : IPresenter<View>{
        fun getData()
    }
}