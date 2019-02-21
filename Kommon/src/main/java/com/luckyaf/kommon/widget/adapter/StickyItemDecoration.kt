package com.luckyaf.kommon.widget.adapter

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-20
 *
 */
open class StickyItemDecoration : RecyclerView.ItemDecoration() {
    companion object {
        const val TAG_STICKY_ITEM = "this is a sticky item"
        const val TAG_NORMAL_ITEM = "this is a normal item"
    }

    /**
     * Adapter ：托管数据集合，为每个子项创建视图
     */
    private var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    /**
     * 标记：UI滚动过程中是否找到标题
     */
    private var mCurrentUIFindStickView: Boolean = false
    /**
     * 标题距离顶部距离
     */
    private var mStickyItemViewMarginTop: Int = 0
    /**
     * 标题布局高度
     */
    private var mItemViewHeight: Int = 0

    /**
     * 标题的视图View
     */
    private var mStickyItemView: View? = null
    /**
     * 承载子项视图的holder
     */
    private var mViewHolder: RecyclerView.ViewHolder? = null
    /**
     * 子项布局管理
     */
    private var mLayoutManager: LinearLayoutManager? = null
    /**
     * 绑定数据的position
     */
    private var mBindDataPosition = -1

    /**
     * 所有标题的position list
     */
    private val mStickyPositionList = ArrayList<Int>()


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        //非空判断
        if (parent.adapter!!.itemCount <= 0) return
        //标记默认不存在小标题
        mCurrentUIFindStickView = false
        //获取布局管理方式
        mLayoutManager = parent.layoutManager as LinearLayoutManager?
        mLayoutManager?.let {
            var i = 0
            val size = parent.childCount
            while (i < size) {
                //viewgroup.getChildCount()：获取所有可见子元素个数。
                //循环得到每一个子项
                val item = parent.getChildAt(i)
                //判断第几个子项是标题（值在Adapter中设置）
                if (item.tag == TAG_STICKY_ITEM) {
                    //标记为true
                    mCurrentUIFindStickView = true
                    //得到标题的 viewHolder
                    getStickyViewHolder(parent)
                    //收集标题的 position
                    cacheStickyViewPosition(i)
                    //标题和父布局的距离。（一般初始化时候先进入）
                    if (item.top <= 0) {
                        //将第一个可见子项位置 和 父布局宽 传入
                        bindDataForStickyView(it.findFirstVisibleItemPosition(), parent.measuredWidth)
                    } else {
                        if (mStickyPositionList.size > 0) {
                            if (mStickyPositionList.size == 1) {//若只缓存一个标题
                                bindDataForStickyView(mStickyPositionList[0], parent.measuredWidth)
                            } else {
                                //得到标题在RecyclerView中的position
                                val currentPosition = it.findFirstVisibleItemPosition() + i
                                //根据标题的position获得所在缓存列表中的索引
                                val indexOfCurrentPosition = mStickyPositionList.lastIndexOf(currentPosition)
                                bindDataForStickyView(mStickyPositionList[indexOfCurrentPosition - 1], parent.measuredWidth)
                            }
                        }
                    }

                    //处理两个标题叠在一起的绘制效果
                    if (item.top > 0 && item.top <= mItemViewHeight) {
                        mStickyItemViewMarginTop = mItemViewHeight - item.top
                    } else {
                        mStickyItemViewMarginTop = 0
                        //得到下一个标题view
                        val nextStickyView = getNextStickyView(parent)
                        //若两标题叠在一起了
                        if (nextStickyView != null && nextStickyView.top <= mItemViewHeight) {
                            //第二个标题盖住第一个标题多少了
                            mStickyItemViewMarginTop = mItemViewHeight - nextStickyView.top
                        }
                    }
                    drawStickyItemView(c)// 准备工作已就绪，开始画出吸附的标题
                    break  //结束循环
                }
                i++
            }
            //取反判断（因为它默认值是false）表示：若存在小标题则进入
            if (!mCurrentUIFindStickView) {
                mStickyItemViewMarginTop = 0
                //判断子元素等于item总数并且缓存数大于0
                if (it.findFirstVisibleItemPosition() + parent.childCount == parent.adapter!!.itemCount && mStickyPositionList.size > 0) {
                    bindDataForStickyView(mStickyPositionList[mStickyPositionList.size - 1], parent.measuredWidth)
                }
                drawStickyItemView(c)//绘制图层
            }
        }


    }


    private fun getNextStickyView(parent: RecyclerView): View? {
        var num = 0
        var m = 0
        val size = parent.childCount
        while (m < size) {
            val view = parent.getChildAt(m)//循环获取每个子项
            if (view.tag == TAG_STICKY_ITEM) {//拿到标题
                num++
            }
            if (num == 2) {//拿到第二个标题 ，就结束循环。
                return view
            }
            m++
        }
        return null
    }

    /**
     * 得到标题的 viewHolder
     * @param recyclerView
     */
    private fun getStickyViewHolder(recyclerView: RecyclerView) {
        if (mAdapter != null) return  //判断是否已创建
        mAdapter = recyclerView.adapter
        //该方法属于Adapter中的重写Override
        mViewHolder = mAdapter?.onCreateViewHolder(recyclerView, 1)
        mStickyItemView = mViewHolder?.itemView//得到布局
        //测量View并且layout
        val widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.UNSPECIFIED)

        //根据父View的MeasureSpec和子view自身的LayoutParams以及padding来获取子View的MeasureSpec
        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                recyclerView.getPaddingLeft() + recyclerView.getPaddingRight(), recyclerView.getLayoutParams().width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                recyclerView.getPaddingTop() + recyclerView.getPaddingBottom(), recyclerView.getLayoutParams().height)
        //进行测量

        mStickyItemView?.measure(childWidth, childHeight)
        //根据测量后的宽高放置位置
        mStickyItemView?.layout(0, 0, recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight())

    }

    /**
     * 收集标题的 position
     * @param i
     */
    private fun cacheStickyViewPosition(i: Int) {
        //得到标题在RecyclerView中的position
        mLayoutManager?.let {
            val position = it.findFirstVisibleItemPosition() + i
            if (!mStickyPositionList.contains(position)) {//防止重复
                mStickyPositionList.add(position)
            }
        }

    }

    /**
     * 给StickyView绑定数据
     * @param position
     */
    private fun bindDataForStickyView(position: Int, width: Int) {
        //已经是吸附位置了 或 视图不存在
        if (mBindDataPosition == position || mViewHolder == null) return
        mViewHolder?.let {
            mBindDataPosition = position
            mAdapter?.onBindViewHolder(it, mBindDataPosition)//改变标题的展示效果，该方法在Adapter中
            measureLayoutStickyItemView(width)//设置布局位置及大小
            mItemViewHeight = it.itemView.bottom - it.itemView.top//计算标题布局高度
        }
    }


    /**
     * 设置布局位置及大小
     * @param parentWidth  父布局宽度
     */
    private fun measureLayoutStickyItemView(parentWidth: Int) {
        mStickyItemView?.isLayoutRequested ?: return
        mStickyItemView?.let {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY)
            val heightSpec: Int

            val layoutParams = it.layoutParams
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }

            it.measure(widthSpec, heightSpec)
            /**
             * view.layout(l,t,r,b) ; 子布局相对于父布局的绘制的位置及大小。
             * l 和 t 是控件左边缘和上边缘相对于父类控件左边缘和上边缘的距离。r 和 b是控件右边缘和下边缘相对于父类控件左边缘和上边缘的距离。
             */
            it.layout(0, 0, it.measuredWidth, it.measuredHeight)

        }
    }

    /**
     * 绘制标题
     * @param canvas
     */
    private fun drawStickyItemView(canvas: Canvas) {
        mStickyItemView ?: return
        val saveCount = canvas.save()//保存当前图层
        canvas.translate(0f, -mStickyItemViewMarginTop.toFloat())//图层转换位移
        mStickyItemView?.draw(canvas)
        canvas.restoreToCount(saveCount) //恢复指定层的图层
    }

}