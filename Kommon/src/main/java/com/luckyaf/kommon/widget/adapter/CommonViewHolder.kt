package com.luckyaf.kommon.widget.adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * 类描述：通用的viewHolder
 * @author Created by luckyAF on 2019/1/14
 *
 */
@Suppress("unused")
class CommonViewHolder (private val itemView: View) {
    companion object {
        fun create(view: View):CommonViewHolder{
            return CommonViewHolder(view)
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

    fun clickView(viewId: Int, block: () -> Unit) {
        var view: View? = mView?.get(viewId)
        //使用缓存的方式减少findViewById的次数
        if (view == null) {
            view = itemView.findViewById(viewId)
            mView?.put(viewId, view)
        }
        view?.setOnClickListener {
            block()
        }
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
    fun setText(viewId: Int, text: CharSequence): CommonViewHolder {
        val view = getView<TextView>(viewId)
        view.text =  text
        //希望可以链式调用
        return this
    }

    fun setHintText(viewId: Int, text: CharSequence): CommonViewHolder {
        val view = getView<TextView>(viewId)
        view.hint =  text
        return this
    }

    /**
     * 设置本地图片
     *
     * @param viewId
     * @param resId
     * @return
     */
    fun setImageResource(viewId: Int, resId: Int): CommonViewHolder {
        val iv = getView<ImageView>(viewId)
        iv.setImageResource(resId)
        return this
    }

    /**
     * 加载图片资源路径
     *
     * @param viewId
     * @param imageLoader
     * @return
     */
    fun setImagePath(viewId: Int, imageLoader: HolderImageLoader): CommonViewHolder {
        val iv = getView<ImageView>(viewId)
        imageLoader.loadImage(iv, imageLoader.path)
        return this
    }

    abstract class HolderImageLoader(val path: String) {

        /**
         * 需要去复写这个方法加载图片
         *
         * @param iv
         * @param path
         */
        abstract fun loadImage(iv: ImageView, path: String)
    }

    /**
     * 设置View的Visibility
     */
    fun setViewVisibility(viewId: Int, visibility: Int): CommonViewHolder {
        getView<View>(viewId).visibility = visibility
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
        }
    }

    fun setBackgroundResource(viewId: Int, resId: Int) {
        val view = getView<View>(viewId)
        view.setBackgroundResource(resId)
    }

    fun setBackgroundColor(viewId: Int, colorId: Int) {
        val view = getView<View>(viewId)
        view.setBackgroundColor(colorId)
    }

}