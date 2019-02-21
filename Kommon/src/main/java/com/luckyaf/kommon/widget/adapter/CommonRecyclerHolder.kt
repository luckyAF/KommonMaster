package com.luckyaf.kommon.widget.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.luckyaf.kommon.extension.otherwise
import com.luckyaf.kommon.extension.yes

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
@Suppress("unused")
class CommonRecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(view: View):CommonRecyclerHolder{
            return CommonRecyclerHolder(view)
        }
    }


    //用于缓存已找的界面
    private var mView: SparseArray<View>? = null

    init {
        mView = SparseArray()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(viewId: Int): T {
        //对已有的view做缓存
        var view: View? = mView?.get(viewId)
        //使用缓存的方式减少findViewById的次数
        if (view == null) {
            view = itemView.findViewById(viewId)
            mView?.put(viewId, view)
        }
        return view as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewGroup> getViewGroup(viewId: Int): T {
        //对已有的view做缓存
        var view: View? = mView?.get(viewId)
        //使用缓存的方式减少findViewById的次数
        if (view == null) {
            view = itemView.findViewById(viewId)
            mView?.put(viewId, view)
        }
        return view as T
    }

    @SuppressLint("SetTextI18n")
    //通用的功能进行封装  设置文本 设置条目点击事件  设置图片
    fun setText(viewId: Int, text: CharSequence): CommonRecyclerHolder {
        val view = getView<TextView>(viewId)
        view.text = "" + text
        //希望可以链式调用
        return this
    }

    fun setHintText(viewId: Int, text: CharSequence): CommonRecyclerHolder {
        val view = getView<TextView>(viewId)
        view.hint = "" + text
        return this
    }

    /**
     * 设置本地图片
     *
     * @param viewId
     * @param resId
     * @return
     */
    fun setImageResource(viewId: Int, resId: Int): CommonRecyclerHolder {
        val iv = getView<ImageView>(viewId)
        iv.setImageResource(resId)
        return this
    }


    /**
     * 设置View的Visibility
     */
    fun setViewVisibility(viewId: Int, visibility: Int): CommonRecyclerHolder {
        getView<View>(viewId).visibility = visibility
        return this
    }

    fun setGone(viewId: Int, gone: Boolean): CommonRecyclerHolder {
        getView<View>(viewId).visibility = gone.yes { View.GONE }.otherwise { View.VISIBLE }
        return this
    }


    /**
     * 设置条目点击事件
     */
    fun setOnItemClickListener(block: () -> Unit) {
        itemView.setOnClickListener {
            block()
        }
    }

    /**
     * 设置条目长按事件
     */
    fun setOnItemLongClickListener(block: () -> Boolean) {
        itemView.setOnLongClickListener {
            block()
            true
        }
    }

}