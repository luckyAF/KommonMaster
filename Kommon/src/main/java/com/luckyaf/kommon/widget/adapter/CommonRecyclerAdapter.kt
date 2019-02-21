package com.luckyaf.kommon.widget.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.luckyaf.kommon.widget.adapter.listener.OnItemClickListener
import com.luckyaf.kommon.widget.adapter.listener.OnItemLongClickListener

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
@Suppress("unused")
abstract class CommonRecyclerAdapter<T>(
        val mContext: Context,
        dataList: List<T>?) : RecyclerView.Adapter<CommonRecyclerHolder>() {

    private val mInflater =  LayoutInflater.from(mContext)
    private val mDataSource: MutableList<T> = dataList?.toMutableList()?:ArrayList()
    private var mClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnItemLongClickListener? = null
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null


    /**
     * 提供itemId
     */
    abstract fun getItemLayoutId(viewType: Int): Int

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    abstract fun bindData(holder: CommonRecyclerHolder, data: T, position: Int)

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mLongClickListener = listener
    }


    open fun updateData(dataList: List<T>){
        mDataSource.clear()
        mDataSource.addAll(dataList)
        notifyDataSetChanged()
    }



    fun replace(position: Int, item: T) {
        if (mDataSource.isEmpty()) {
            return
        }
        if (position < 0 || position >= mDataSource.size) {
            return
        }
        mDataSource[position] =  item
        notifyItemChanged(position)
    }
    fun add(position: Int, item: T) {
        mDataSource.add(position, item)
        notifyItemInserted(position)
    }

    fun delete(position: Int) {
        mDataSource.removeAt(position)
        notifyItemRemoved(position)
    }


//    /**
//     * 用来判断item是否为真实数据项，除了头部、尾部、系统尾部等非真实数据项，结构为:
//     * item_header - item_data - item_footer - item_sys_footer
//     * @param position
//     * @return true:将保留LayoutManager的设置
//     * false:该item将会横跨整行(对GridLayoutManager,StaggeredLayoutManager将很有用)
//     */
//    private fun isDataItemView(position: Int): Boolean {
//        val shc = this.getSysHeaderViewCount()
//        val hc = this.getHeaderViewCount()
//        val hAll = shc + hc
//        val dc = this.getDataSectionItemCount()
//        val isHeaderOrFooter = position >= 0 && position >= hAll && position < hAll + dc
//        return if (!isHeaderOrFooter) {
//            isHeaderOrFooter
//        } else !this.isFullSpanWithItemView(position - hAll)
//        //这里需要取反
//    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (mRecyclerView == recyclerView) {
            return
        }
        mRecyclerView = recyclerView
        mLayoutManager = recyclerView.layoutManager
        adapterGridLayoutManager()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecyclerView = null
        this.release()
    }

    private fun adapterGridLayoutManager() {
        val layoutManager = mRecyclerView?.layoutManager
        layoutManager?:return
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isFullSpanWithItemView(position)) {
                        layoutManager.spanCount
                    } else {
                        spanSizeLookup.getSpanSize(position)
                    }
                }
            }
        }
    }


    private fun adapterStaggeredGridLayoutManager(holder: CommonRecyclerHolder) {
        val layoutManager = mRecyclerView?.layoutManager
        layoutManager?:return
        if (layoutManager is StaggeredGridLayoutManager) {
            val layoutParams = holder.itemView.layoutParams
            val position = holder.adapterPosition
            if ( layoutParams is StaggeredGridLayoutManager.LayoutParams && isFullSpanWithItemView(position)) {
                layoutParams.isFullSpan = true
            }
        }
    }

    /**
     * 设置数据域item是否横跨
     * @param position
     * @return
     */
    open fun isFullSpanWithItemView(position: Int): Boolean {
        return false
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerHolder {
        //创建view
        val holder = CommonRecyclerHolder(mInflater.inflate(getItemLayoutId(viewType), parent, false))
        if(null != mClickListener) {
            holder.itemView.setOnClickListener{
                mClickListener?.onItemClick(holder.itemView, holder.layoutPosition)
            }
        }
        if(null != mLongClickListener){
            holder.itemView.setOnLongClickListener {
                mLongClickListener?.onItemLongClick(holder.itemView, holder.layoutPosition)
                 true
            }
        }
        return holder
    }


    override fun onBindViewHolder(holder: CommonRecyclerHolder, position: Int) {
        //绑定数据
        bindData(holder, mDataSource[position], position)
    }


    fun getItem(position: Int): T {
        return mDataSource[position]
    }


    override fun getItemCount(): Int {
        return mDataSource.size
    }

    private fun release() {
       mDataSource.clear()

    }


}