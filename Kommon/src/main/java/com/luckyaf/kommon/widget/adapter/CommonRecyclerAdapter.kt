package com.luckyaf.kommon.widget.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
abstract class CommonRecyclerAdapter<T>(
        var mContext: Context,
        private var mLayoutId: Int) : RecyclerView.Adapter<CommonRecyclerHolder>() {

    private var mInflater: LayoutInflater? = null
    private var mTypeSupport: MultipleType<T>? = null

    private var mDataSource: ArrayList<T> //条目布局

    init {
        mInflater = LayoutInflater.from(mContext)
        mDataSource = ArrayList()
    }

    fun updateData(dataList: List<T>){
        mDataSource.clear()
        mDataSource.addAll(dataList)
        notifyDataSetChanged()

    }

    //自带list
    constructor(context: Context, dataList: MutableList<T> ,layoutId:Int) :
            this(context, layoutId) {
        updateData(dataList)
    }


    //需要多布局
    constructor(context: Context, dataList: ArrayList<T>, typeSupport: MultipleType<T>) :
            this(context, -1) {
        this.mTypeSupport = typeSupport
        updateData(dataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerHolder {
        if (mTypeSupport != null) {
            //需要多布局
            mLayoutId = viewType
        }
        //创建view
        val view = mInflater?.inflate(mLayoutId, parent, false)
        return CommonRecyclerHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        //多布局问题
        return mTypeSupport?.getLayoutId(mDataSource[position], position)
                ?: super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: CommonRecyclerHolder, position: Int) {
        //绑定数据
        bindData(holder, mDataSource[position], position)
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: CommonRecyclerHolder, data: T, position: Int)

    override fun getItemCount(): Int {
        return mDataSource.size
    }

}