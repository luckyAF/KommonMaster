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
        val mContext: Context,
        dataList: List<T>?
) : BaseAdapter() {

    private val mInflater =  LayoutInflater.from(mContext)
    private val mDataSource: MutableList<T> = dataList?.toMutableList()?:ArrayList()



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
    protected abstract fun bindData(holder: CommonViewHolder, data: T, position: Int)



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder:CommonViewHolder
        val view: View
        if(null == convertView){
            view = mInflater.inflate(getItemLayoutId(getItemViewType(position)), null)
            viewHolder = CommonViewHolder.create(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as CommonViewHolder
        }
        bindData(viewHolder,mDataSource[position],position)
        return view
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