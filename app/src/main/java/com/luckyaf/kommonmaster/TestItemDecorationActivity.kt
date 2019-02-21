package com.luckyaf.kommonmaster

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.widget.adapter.CommonRecyclerAdapter
import com.luckyaf.kommon.widget.adapter.CommonRecyclerHolder
import com.luckyaf.kommon.widget.adapter.StickyItemDecoration
import kotlinx.android.synthetic.main.activity_test_recycler.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-20
 *
 */
class TestItemDecorationActivity : BaseActivity() {

    private lateinit var adapter: CommonRecyclerAdapter<Int>

    private val mDataSource = ArrayList<Int>()


    override fun initData(bundle: Bundle?) {
    }

    override fun getLayoutId() = R.layout.activity_test_recycler

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
//        val layoutManager = GridLayoutManager(instance,3)

        //recyclerView.layoutManager = GridLayoutManager(instance,3)
        recyclerView.layoutManager = LinearLayoutManager(instance)
        adapter = object : CommonRecyclerAdapter<Int>(instance, mDataSource) {

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_common_text
            }

            override  fun isFullSpanWithItemView(position: Int): Boolean {
                return position % 8 == 0
            }

            override fun bindData(holder: CommonRecyclerHolder, data: Int, position: Int) {
                holder.setText(R.id.tvContent, "num = $data")
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.kommon_white))
                if (data % 8 == 0) {
                    holder.itemView.tag = StickyItemDecoration.TAG_STICKY_ITEM
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.kommon_blue))
                }else{
                    holder.itemView.tag = StickyItemDecoration.TAG_NORMAL_ITEM
                }
            }
        }
        //  在setAdapter之前。
        recyclerView.addItemDecoration(StickyItemDecoration())
        recyclerView.adapter = adapter

    }

    override fun start() {
        loadData()
    }

    private fun loadData(number: Int = 1) {
        val numberRange = 0.until(150)
        mDataSource.clear()
        mDataSource.addAll(numberRange.map { it })
        adapter.updateData(mDataSource)
    }


}