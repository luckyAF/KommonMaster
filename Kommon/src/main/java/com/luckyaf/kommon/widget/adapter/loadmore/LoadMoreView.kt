package com.luckyaf.kommon.widget.adapter.loadmore

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import com.luckyaf.kommon.widget.adapter.CommonRecyclerHolder

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/28
 *
 */
abstract class LoadMoreView {
    companion object {
        const val STATUS_DEFAULT = 1
        const val STATUS_LOADING = 2
        const val STATUS_FAIL = 3
        const val STATUS_END = 4
    }

    private var mLoadMoreStatus = STATUS_DEFAULT
    private var mLoadMoreEndGone = false

    fun setLoadMoreStatus(loadMoreStatus: Int) {
        this.mLoadMoreStatus = loadMoreStatus
    }

    fun getLoadMoreStatus(): Int {
        return mLoadMoreStatus
    }

    fun convert(holder: CommonRecyclerHolder) {
        when (mLoadMoreStatus) {
            STATUS_LOADING -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, true)
                visibleLoadEnd(holder, false)
            }
            STATUS_FAIL -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, true)
                visibleLoadEnd(holder, false)
            }
            STATUS_END -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, false)
                visibleLoadEnd(holder, true)
            }
            STATUS_DEFAULT -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, false)
                visibleLoadEnd(holder, false)
            }
        }
    }

    private fun visibleLoading(holder: CommonRecyclerHolder, visible: Boolean) {
        holder.setGone(getLoadingViewId(), !visible)
    }

    private fun visibleLoadFail(holder: CommonRecyclerHolder, visible: Boolean) {
        holder.setGone(getLoadFailViewId(), !visible)
    }

    private fun visibleLoadEnd(holder: CommonRecyclerHolder, visible: Boolean) {
        val loadEndViewId = getLoadEndViewId()
        if (loadEndViewId != 0) {
            holder.setGone(loadEndViewId, !visible)
        }
    }

    fun setLoadMoreEndGone(loadMoreEndGone: Boolean) {
        this.mLoadMoreEndGone = loadMoreEndGone
    }

    fun isLoadEndMoreGone(): Boolean {
        return if (getLoadEndViewId() == 0) {
            true
        } else mLoadMoreEndGone
    }



    /**
     * load more layout
     *
     * @return
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * loading view
     *
     * @return
     */
    @IdRes
    protected abstract fun getLoadingViewId(): Int

    /**
     * load fail view
     *
     * @return
     */
    @IdRes
    protected abstract fun getLoadFailViewId(): Int

    /**
     * load end view, you can return 0
     *
     * @return
     */
    @IdRes
    protected abstract fun getLoadEndViewId(): Int

}