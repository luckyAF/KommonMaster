package com.luckyaf.kommon.view.recyclerview.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.luckyaf.kommon.view.recyclerview.MultipleType
import com.luckyaf.kommon.view.recyclerview.ViewHolder

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
abstract class CommonAdapter<T>(
        var mContext: Context,
        var mData: ArrayList<T>, //条目布局
        private var mLayoutId: Int) : RecyclerView.Adapter<ViewHolder>() {

    protected var mInflater: LayoutInflater? = null
    private var mTypeSupport: MultipleType<T>? = null

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    //需要多布局
    constructor(context: Context, data: ArrayList<T>, typeSupport: MultipleType<T>) :
            this(context, data, -1) {
        this.mTypeSupport = typeSupport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mTypeSupport != null) {
            //需要多布局
            mLayoutId = viewType
        }
        //创建view
        val view = mInflater?.inflate(mLayoutId, parent, false)
        return ViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {
        //多布局问题
        return mTypeSupport?.getLayoutId(mData[position], position)
                ?: super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //绑定数据
        bindData(holder, mData[position], position)



    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: ViewHolder, data: T, position: Int)

    override fun getItemCount(): Int {
        return mData.size
    }

}