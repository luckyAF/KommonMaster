package com.luckyaf.kommon.sample

import android.content.Context
import com.luckyaf.kommon.R
import com.luckyaf.kommon.view.recyclerview.MultipleType
import com.luckyaf.kommon.view.recyclerview.ViewHolder
import com.luckyaf.kommon.view.recyclerview.adapter.CommonAdapter

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/12
 *
 */
class MultipleAdapter(context: Context, list: ArrayList<SampleData>)
    :CommonAdapter<SampleData>(context,list,object : MultipleType<SampleData> {
    override fun getLayoutId(item: SampleData, position: Int): Int {
       return when {
            item.name == "hh" -> R.layout.adapter_sample
            else ->
                throw IllegalAccessException("Api 解析出错了，出现其他类型")

        }
    }

})
{
    override fun bindData(holder: ViewHolder, data: SampleData, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}