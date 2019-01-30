package com.luckyaf.kommon.widget.adapter.loadmore

import com.luckyaf.kommon.R

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/28
 *
 */
class SimpleLoadMoreView :LoadMoreView() {
    override fun getLayoutId() = R.layout.layout_recycler_load_more_view
    override fun getLoadingViewId() = R.id.load_more_loading_view

    override fun getLoadFailViewId() = R.id.load_more_load_fail_view

    override fun getLoadEndViewId() = R.id.load_more_load_end_view

}