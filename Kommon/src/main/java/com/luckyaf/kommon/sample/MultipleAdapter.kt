package com.luckyaf.kommon.sample

import android.content.Context
import com.luckyaf.kommon.R
import com.luckyaf.kommon.widget.adapter.MultipleType
import com.luckyaf.kommon.widget.adapter.CommonRecyclerHolder
import com.luckyaf.kommon.widget.adapter.CommonRecyclerAdapter

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/12
 *
 */
class MultipleAdapter(context: Context, list: ArrayList<SampleData>)
    : CommonRecyclerAdapter<SampleData>(context,list,object : MultipleType<SampleData> {
    override fun getLayoutId(item: SampleData, position: Int): Int {
       return when {
            item.name == "hh" -> R.layout.adapter_sample
            else ->
                throw IllegalAccessException("Api 解析出错了，出现其他类型")

        }
    }

})
{
    override fun bindData(holder: CommonRecyclerHolder, data: SampleData, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}