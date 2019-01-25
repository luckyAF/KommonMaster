package com.luckyaf.kommonmaster

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.BaseAdapter
import com.luckyaf.kommon.base.BaseActivity
import com.luckyaf.kommon.extension.DEBUG
import com.luckyaf.kommon.extension.clickWithTrigger
import com.luckyaf.kommon.extension.toJson
import com.luckyaf.kommon.extension.yes
import com.luckyaf.kommon.net.BaseResponse
import com.luckyaf.kommon.net.HttpCallback
import com.luckyaf.kommon.net.NetManager
import com.luckyaf.kommon.net.execute
import kotlinx.android.synthetic.main.activity_test_net.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/25
 *
 */
class TestNetActivity :BaseActivity(){
    private lateinit var viewModel: HttpViewModel

    override fun getLayoutId() = R.layout.activity_test_net

    override fun initData(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(HttpViewModel::class.java)
    }

    override fun initView() {

        viewModel.data.observe(this, Observer<String>{
            tvResponse.text = it
        })
        btnGet.clickWithTrigger {
            testGet()
        }
        btnPost.clickWithTrigger {
            testPost()
        }
        viewModel.isLoading.observe(this, Observer{
            it?.yes {

            }
        })

    }

    override fun start() {
    }


    private fun testGet(){
                    GlobalScope.launch {
                        val result = NetManager.get(this,"http://traceback.openhema.com/trace/login")
                                .params("username" to "test003",
                                        "password" to 333333
                                )
                                .execute<RestResult<ProductListBean>>().await()
                        result.DEBUG("result")
            }






    }

    private fun testPost(){
        NetManager.post(this,"http://traceback.openhema.com/trace/product/list")
                .params("warehouseId" to 7,
                        "token" to "9fe19ef3baa9b935783719c7316afb22"
                ).execute(object : HttpCallback<RestResult<ProductListBean>>{
                    override fun onSuccess(data: RestResult<ProductListBean>) {
                        data.DEBUG()
                        if(data.status == 0){
                            viewModel.data.postValue(data.data.toJson())
                        }
                    }

                })
    }

    class HttpViewModel : ViewModel() {
        val data = MutableLiveData<String>()
        val isLoading = MutableLiveData<Boolean>()
    }
}
data class RestResult<T>(
        var message: String = "",
        var status: Int = 0,
        var data: T
)

data class ProductListBean(
        val list: List<ProductBean>
)


data class ProductBean(
        val productId:Int,        //产品id
        val productName:String,   //产品名
        val level2:Int,
        val level3:Int,           //  一垛有多少箱
        val level4:Int
)