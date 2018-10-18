package com.luckyaf.kommon.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.luckyaf.kommon.R
import com.luckyaf.kommon.utils.ToastUtil
import com.luckyaf.kommon.view.recyclerview.ViewHolder
import com.luckyaf.kommon.view.recyclerview.adapter.CommonAdapter

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
 class SampleAdapter(context: Context,list: ArrayList<SampleData>): CommonAdapter<SampleData>(context,list,R.layout.adapter_sample) {

    override fun bindData(holder: ViewHolder, data: SampleData, position: Int) {
        holder.getView<TextView>(R.id.txtSample).text = data.name

        holder.setOnItemClickListener{
            ToastUtil.show(mContext,data.desc)
        }
    }
}



