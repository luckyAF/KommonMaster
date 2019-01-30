package com.luckyaf.kommon.widget.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.luckyaf.kommon.extension.Equeals
import com.luckyaf.kommon.extension.NotMoreThan
import java.util.ArrayList

/**
 * 类描述：
 *   headerView footerView  emptyView  loadMore
 * @author Created by luckyAF on 2019/1/28
 *
 */
abstract class SmartRecyclerAdapter<T>(
        private var mContext: Context,
        private var mLayoutId: Int) : RecyclerView.Adapter<CommonRecyclerHolder>() {

    private val mInflater = LayoutInflater.from(mContext)
    private var mTypeSupport: MultipleType<T>? = null
    private var mDataSource = ArrayList<T>()

    //header footer
    private var mHeaderLayout: LinearLayout? = null
    private var mFooterLayout: LinearLayout? = null
    //empty
    private var mEmptyLayout: FrameLayout? = null
    private var mIsUseEmpty = true                   // 是否启用EmptyView
    private var mHeadAndEmptyEnable: Boolean = false // 数据为空时 要不要显示header
    private var mFootAndEmptyEnable: Boolean = false // 数据为空时 要不要显示footer


    //  itemType
    val HEADER_VIEW = -233111
    val LOADING_VIEW = -233222
    val FOOTER_VIEW = -233333
    val EMPTY_VIEW = -233555


    //自带 list
    constructor(context: Context, dataList: MutableList<T>, layoutId: Int) :
            this(context, layoutId) {
        initData(dataList)
    }

    //需要 多布局
    constructor(context: Context, dataList: ArrayList<T>, typeSupport: MultipleType<T>) :
            this(context, -1) {
        this.mTypeSupport = typeSupport
        initData(dataList)
    }


    private fun initData(dataList: List<T>) {
        mDataSource.clear()
        mDataSource.addAll(dataList)
    }


    fun updateData(dataList: List<T>) {
        this.mDataSource.clear()
        this.mDataSource.addAll(dataList)
        notifyDataSetChanged()
    }


    /**
     * 获取item数
     * 如果有数据   则为 header + 数据 + footer + loaderMore
     * 如果没数据   则为
     */
    override fun getItemCount(): Int {
        var count: Int
        if (getEmptyViewCount() == 1) {  // 假如要显示emptyView了
            count = 1
            if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                count++
            }
            if (mFootAndEmptyEnable && getFooterLayoutCount() != 0) {
                count++
            }
        } else {
            count = getHeaderLayoutCount() + mDataSource.size + getFooterLayoutCount()
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        if (getEmptyViewCount() == 1) {   // 假如要显示emptyView
            // 设置了 header 并且设置了emptyview时 header仍然显示
            val header = mHeadAndEmptyEnable && getHeaderLayoutCount() != 0
            return when (position) {
                0 -> if (header) {
                    HEADER_VIEW
                } else {
                    EMPTY_VIEW
                }
                1 -> if (header) {
                    EMPTY_VIEW
                } else {
                    FOOTER_VIEW
                }
                2 -> FOOTER_VIEW
                else -> EMPTY_VIEW
            }
        }
        val numHeaders = getHeaderLayoutCount()
        if (position < numHeaders) {
            return HEADER_VIEW
        } else {
            var adjPosition = position - numHeaders
            val adapterCount = mDataSource.size
            if (adjPosition < adapterCount) {   // 如果显示具体数据
                return mTypeSupport?.getLayoutId(mDataSource[adjPosition], adjPosition)
                        ?: super.getItemViewType(adjPosition)
            } else {
                adjPosition = adjPosition - adapterCount
                val numFooters = getFooterLayoutCount()
                return if (adjPosition < numFooters) {
                    FOOTER_VIEW
                } else {
                    LOADING_VIEW
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerHolder {
        return when (viewType) {
            HEADER_VIEW -> CommonRecyclerHolder.create(mHeaderLayout!!)
            EMPTY_VIEW -> CommonRecyclerHolder.create(mEmptyLayout!!)
            FOOTER_VIEW -> CommonRecyclerHolder.create(mFooterLayout!!)
            else -> getCommonItem(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: CommonRecyclerHolder, position: Int) {
        val viewType = holder.itemViewType
        val truePosition = position - getHeaderLayoutCount()
        when (viewType) {
            LOADING_VIEW -> {}
            HEADER_VIEW -> {
            }
            EMPTY_VIEW -> {
            }
            FOOTER_VIEW -> {
            }
            else -> bindData(holder, mDataSource[truePosition], truePosition)
        }
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: CommonRecyclerHolder, data: T, position: Int)


    fun getCommonItem(parent: ViewGroup, viewType: Int): CommonRecyclerHolder {
        var layoutId = mLayoutId
        if (mTypeSupport != null) {
            //需要多布局
            layoutId = viewType
        }
        //创建view
        val view = mInflater.inflate(layoutId, parent, false)
        return CommonRecyclerHolder(view)
    }


    private fun getItemView(@LayoutRes layoutResId: Int, parent: ViewGroup): View {
        return mInflater.inflate(layoutResId, parent, false)
    }











    private fun isFullScreen(llm: LinearLayoutManager): Boolean {
        return llm.findLastCompletelyVisibleItemPosition() + 1 != itemCount
                || llm.findFirstCompletelyVisibleItemPosition() != 0
    }


    /**
     * 得到数组中最大值
     */
    private fun getTheBiggestNumber(numbers: IntArray?): Int {
        var tmp = -1
        numbers ?: return tmp
        if (numbers.isEmpty()) {
            return tmp
        }
        for (num in numbers) {
            if (num > tmp) {
                tmp = num
            }
        }
        return tmp
    }


    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    private fun getHeaderLayoutCount(): Int {
        return when (mHeaderLayout?.childCount != 0) {
            true -> 1
            false -> 0
        }
    }

    /**
     * if addFooterView will be return 1, if not will be return 0
     */
    private fun getFooterLayoutCount(): Int {
        return when (mFooterLayout?.childCount != 0) {
            true -> 1
            false -> 0
        }
    }

    /**
     * Return root layout of header
     */

    fun getHeaderLayout(): LinearLayout? {
        return mHeaderLayout
    }

    /**
     * Return root layout of footer
     */
    fun getFooterLayout(): LinearLayout? {
        return mFooterLayout
    }

    /**
     * Append header to the rear of the mHeaderLayout.
     *
     * @param header
     */
    fun addHeaderView(header: View): Int {
        return addHeaderView(header, -1)
    }

    /**
     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
     * When index = -1 or index >= child count in mHeaderLayout,
     * the effect of this method is the same as that of [.addHeaderView].
     *
     * @param header
     * @param index  the position in mHeaderLayout of this header.
     * When index = -1 or index >= child count in mHeaderLayout,
     * the effect of this method is the same as that of [.addHeaderView].
     */
    fun addHeaderView(header: View, index: Int): Int {
        return addHeaderView(header, index, LinearLayout.VERTICAL)
    }

    /**
     * @param header
     * @param index
     * @param orientation
     */
    fun addHeaderView(header: View, index: Int, orientation: Int): Int {
        var nowIndex = index
        if (mHeaderLayout == null) {
            mHeaderLayout = LinearLayout(header.context)
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout?.orientation = LinearLayout.VERTICAL
                mHeaderLayout?.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                mHeaderLayout?.orientation = LinearLayout.HORIZONTAL
                mHeaderLayout?.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = mHeaderLayout!!.childCount
        if (nowIndex < 0 || nowIndex > childCount) {
            nowIndex = childCount
        }
        mHeaderLayout!!.addView(header, nowIndex)
        if (mHeaderLayout!!.childCount == 1) {
            val position = getHeaderViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return index
    }

    fun setHeaderView(header: View): Int {
        return setHeaderView(header, 0, LinearLayout.VERTICAL)
    }

    fun setHeaderView(header: View, index: Int): Int {
        return setHeaderView(header, index, LinearLayout.VERTICAL)
    }

    fun setHeaderView(header: View, index: Int, orientation: Int): Int {
        if (mHeaderLayout == null) {
            return addHeaderView(header, index, orientation)
        }
        if (mHeaderLayout?.childCount.NotMoreThan(index)) {
            return addHeaderView(header, index, orientation)
        }
        mHeaderLayout?.removeViewAt(index)
        mHeaderLayout?.addView(header, index)
        return index

    }

    /**
     * Append footer to the rear of the mFooterLayout.
     *
     * @param footer
     */
    fun addFooterView(footer: View): Int {
        return addFooterView(footer, -1, LinearLayout.VERTICAL)
    }

    fun addFooterView(footer: View, index: Int): Int {
        return addFooterView(footer, index, LinearLayout.VERTICAL)
    }

    fun addFooterView(footer: View, index: Int, orientation: Int): Int {
        var nowIndex = index
        if (mFooterLayout == null) {
            mFooterLayout = LinearLayout(footer.context)
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout?.orientation = LinearLayout.VERTICAL
                mFooterLayout?.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                mFooterLayout?.orientation = LinearLayout.HORIZONTAL
                mFooterLayout?.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = mFooterLayout?.childCount ?: 0
        if (nowIndex < 0 || nowIndex > childCount) {
            nowIndex = childCount
        }
        mFooterLayout?.addView(footer, nowIndex)
        if (mFooterLayout?.childCount.Equeals(1)) {
            val position = getFooterViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return nowIndex
    }

    fun setFooterView(header: View): Int {
        return setFooterView(header, 0, LinearLayout.VERTICAL)
    }

    fun setFooterView(header: View, index: Int): Int {
        return setFooterView(header, index, LinearLayout.VERTICAL)
    }

    fun setFooterView(header: View, index: Int, orientation: Int): Int {
        if (mFooterLayout?.childCount ?: -1 < index) {
            return addFooterView(header, index, orientation)
        } else {
            mFooterLayout?.removeViewAt(index)
            mFooterLayout?.addView(header, index)
            return index
        }
    }

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    fun removeHeaderView(header: View) {
        if (getHeaderLayoutCount() == 0) {
            return
        }
        mHeaderLayout?.removeView(header)
        if (mHeaderLayout?.childCount ?: -1 == 0) {
            val position = getHeaderViewPosition()
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    fun removeFooterView(footer: View) {
        if (getFooterLayoutCount() == 0) return

        mFooterLayout?.removeView(footer)
        if (mFooterLayout?.childCount == 0) {
            val position = getFooterViewPosition()
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * remove all header view from mHeaderLayout and set null to mHeaderLayout
     */
    fun removeAllHeaderView() {
        if (getHeaderLayoutCount() == 0) return

        mHeaderLayout?.removeAllViews()
        val position = getHeaderViewPosition()
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    /**
     * remove all footer view from mFooterLayout and set null to mFooterLayout
     */
    fun removeAllFooterView() {
        if (getFooterLayoutCount() == 0) return

        mFooterLayout?.removeAllViews()
        val position = getFooterViewPosition()
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    private fun getHeaderViewPosition(): Int {
        //Return to header view notify position
        if (getEmptyViewCount() == 1) {
            if (mHeadAndEmptyEnable) {
                return 0
            }
        } else {
            return 0
        }
        return -1
    }

    private fun getFooterViewPosition(): Int {
        //Return to footer view notify position
        if (getEmptyViewCount() == 1) {
            var position = 1
            if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                position++
            }
            if (mFootAndEmptyEnable) {
                return position
            }
        } else {
            return getHeaderLayoutCount() + mDataSource.size
        }
        return -1
    }


    // empty

    /**
     * 设置emptyView
     */
    fun setEmptyView(layoutResId: Int, viewGroup: ViewGroup) {
        val view = LayoutInflater.from(viewGroup.context).inflate(layoutResId, viewGroup, false)
        setEmptyView(view)
    }

    /**
     * 设置emptyView
     */
    fun setEmptyView(emptyView: View) {
        var insert = false
        if (mEmptyLayout == null) {
            mEmptyLayout = FrameLayout(emptyView.context)
            val layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT)
            val lp = emptyView.layoutParams
            if (lp != null) {
                layoutParams.width = lp.width
                layoutParams.height = lp.height
            }
            mEmptyLayout?.layoutParams = layoutParams
            insert = true
        }
        mEmptyLayout?.removeAllViews()
        mEmptyLayout?.addView(emptyView)
        mIsUseEmpty = true
        if (insert) {
            if (getEmptyViewCount() == 1) {
                var position = 0
                if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                    position++
                }
                notifyItemInserted(position)
            }
        }
    }

    /**
     * 设置显示emptyView时 header和footer人是否显示
     * @param isHeadAndEmpty
     */
    fun setHeaderAndEmpty(isHeadAndEmpty: Boolean) {
        setHeaderFooterEmpty(isHeadAndEmpty, false)
    }

    /**
     * 设置显示emptyView时 header和footer人是否显示
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     */
    fun setHeaderFooterEmpty(isHeadAndEmpty: Boolean, isFootAndEmpty: Boolean) {
        mHeadAndEmptyEnable = isHeadAndEmpty
        mFootAndEmptyEnable = isFootAndEmpty
    }


    fun isUseEmpty(isUseEmpty: Boolean) {
        mIsUseEmpty = isUseEmpty
    }

    fun getEmptyView(): View? {
        return mEmptyLayout
    }

    /**
     * 获取 emptyView Count
     */
    private fun getEmptyViewCount(): Int {
        // 假如 未设置 emptyView   mEmptyLayout为空 或child为0
        if (mEmptyLayout?.childCount ?: 0 == 0) {
            return 0
        }
        // 假如未启用 emptyView
        if (!mIsUseEmpty) {
            return 0
        }
        // 假如有数据
        return if (mDataSource.size != 0) {
            0
        } else 1
    }


}