package com.luckyaf.kommon.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * 类描述： 通用BaseAdapter
 * @author Created by luckyAF on 2019/1/14
 *
 */
@Suppress("unused")
abstract class CommonBaseAdapter<T>(
        private val mContext: Context,
        private var mLayoutId: Int
) : BaseAdapter() {

    private var mDataSource: ArrayList<T> //条目布局
    private var mInflater: LayoutInflater? = null
    private var mTypeSupport: MultipleType<T>? = null


    init {
        mInflater = LayoutInflater.from(mContext)
        mDataSource = ArrayList()
        mDataSource.clear()
    }

    protected fun initData(dataList: List<T>){
        mDataSource.clear()
        mDataSource.addAll(dataList)
    }

    open fun updateData(dataList: List<T>){
        mDataSource.clear()
        mDataSource.addAll(dataList)
        notifyDataSetChanged()
    }

    //自带list
    constructor(context: Context, dataList: MutableList<T>, layoutId:Int) :
            this(context, layoutId) {
        initData(dataList)
    }

    //需要多布局
    constructor(context: Context, dataList: MutableList<T>, typeSupport: MultipleType<T>) :
            this(context, -1) {
        this.mTypeSupport = typeSupport
        initData(dataList)
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: CommonViewHolder, data: T, position: Int)



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder:CommonViewHolder
        val view: View
        if(null == convertView){
            view = mInflater!!.inflate(mLayoutId, null)
            viewHolder = CommonViewHolder.create(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as CommonViewHolder
        }
        bindData(viewHolder,mDataSource[position],position)
        return view
    }

    override fun getItemViewType(position: Int): Int {
        //多布局问题
        return mTypeSupport?.getLayoutId(mDataSource[position], position)
                ?: super.getItemViewType(position)
    }

    override fun getItem(position: Int): T {
        return mDataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return  mDataSource.size
    }


}