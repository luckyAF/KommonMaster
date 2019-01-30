package com.luckyaf.kommonmaster

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.extension.DEBUG
import com.luckyaf.kommon.manager.AppExecutors
import com.luckyaf.kommon.widget.adapter.CommonRecyclerHolder
import com.luckyaf.kommon.widget.adapter.SmartRecyclerAdapter
import kotlinx.android.synthetic.main.activity_test_recycler.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/29
 *
 */
class TestRecyclerActivity : BaseActivity() {

    private val appExecutors = AppExecutors()
    private lateinit var adapter: SmartRecyclerAdapter<String>

    private val mDataSource = ArrayList<String>()

    override fun getLayoutId() = R.layout.activity_test_recycler


    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(instance)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        adapter = object : SmartRecyclerAdapter<String>(instance, R.layout.item_common_text) {
            override fun bindData(holder: CommonRecyclerHolder, data: String, position: Int) {
                holder.setText(R.id.tvContent, data)
                holder.setOnItemClickListener {
                    loadData(position)
                }
            }
        }
        val headerView = getHeaderView(0, View.OnClickListener {
            adapter.addHeaderView(getHeaderView(1, getRemoveHeaderListener()), 0) })
        adapter.addHeaderView(headerView)

        val footerView = getFooterView(0, View.OnClickListener {
            adapter.addFooterView(getFooterView(1, getRemoveFooterListener()), 0) })
        adapter.addFooterView(footerView, 0)

        adapter.setEmptyView(R.layout.empty_view, recyclerView.parent as ViewGroup)
        adapter.setHeaderFooterEmpty(true,true)
        recyclerView.adapter = adapter


    }

    override fun start() {
        loadData()
    }


    private fun loadData(number:Int = 1) {
        appExecutors.runOnIoThread {
            Thread.sleep(2000)
            appExecutors.runOnMainThread {
                val numberRange = 0.until(mDataSource.size + number)
                mDataSource.clear()
                mDataSource.addAll(
                        numberRange.map {
                            "Item $it"
                        }
                )
                adapter.updateData(mDataSource)
            }
        }
    }


    private fun getHeaderView(type: Int, listener: View.OnClickListener): View {
        val view = layoutInflater.inflate(R.layout.header_view, recyclerView.parent as ViewGroup, false)
        if (type == 1) {
            val imageView = view.findViewById(R.id.image) as ImageView
            imageView.setImageResource(R.drawable.ic_delete)
        }
        view.setOnClickListener(listener)
        return view
    }

    private fun getFooterView(type: Int, listener: View.OnClickListener): View {
        val view = layoutInflater.inflate(R.layout.footer_view, recyclerView.parent as ViewGroup, false)
        if (type == 1) {
            val imageView = view.findViewById(R.id.image) as ImageView
            imageView.setImageResource(R.drawable.ic_delete)
        }
        view.setOnClickListener(listener)
        return view
    }

    private fun getRemoveHeaderListener(): View.OnClickListener {
        return View.OnClickListener { v -> adapter.removeHeaderView(v) }
    }


    private fun getRemoveFooterListener(): View.OnClickListener {
        return View.OnClickListener { v -> adapter.removeFooterView(v) }
    }


}