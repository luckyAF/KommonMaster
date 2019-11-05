package com.luckyaf.kommonmaster

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.luckyaf.kommon.callback._subscribe
import com.luckyaf.kommon.extension.*
import com.luckyaf.kommon.http.SmartHttp
import kotlinx.android.synthetic.main.activity_test_net.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/25
 *
 */
class TestNetActivity : SmartActivity() {


    private lateinit var viewModel: HttpViewModel

    override fun getLayoutId() = R.layout.activity_test_net

    override fun initData(bundle: Bundle) {
        viewModel = ViewModelProviders.of(this).get(HttpViewModel::class.java)
    }

    override fun initView(savedInstanceState: Bundle, contentView: View) {
        viewModel.data.observe(this, Observer<String> {
            tvResponse.text = it
        })
        btnGet.clickWithTrigger {
            testGet()
        }
        btnPost.clickWithTrigger {
            testPost()
        }
        btnZhuhu.clickWithTrigger {
            testZhihu()
        }
        btnRxGet.clickWithTrigger {
            testRxGet()
        }
        viewModel.isLoading.observe(this, Observer {
            it?.yes {

            }
        })

    }

    override fun start() {
    }


    private fun testGet() {
        launchIO(
                {
                    SmartHttp.get()
                            .url("http://traceback.openhema.com/trace/login")
                            .params("username" to "test003",
                                    "password" to 333333
                            )
                            .suspendRequest<RestResult<ProductListBean>>()
                }, {

            showMessage("成功")

        }, {
            it.DEBUG("exception")
            showMessage(it.message ?: "异常")
        })


    }

    private fun testPost() {
        SmartHttp.post()
                .url("http://traceback.openhema.com/trace/product/list")
                .params("warehouseId" to 7,
                        "token" to "9fe19ef3baa9b935783719c7316afb22"
                ).request<RestResult<ProductListBean>> {
                    success {
                        showMessage("成功")
                        if (it?.status == 0) {
                            viewModel.data.postValue(it.data.toJson())
                        }

                    }
                    error {
                        showMessage(it.message ?: "失败")

                    }
                }

    }


    private fun testZhihu() {
        SmartHttp.get()
                .url("http://news-at.zhihu.com/api/4/news/latest")
                .request<ZhihuDaily> {
                    success {
                        it?.date.DEBUG()
                        it?.let {
                            //showData(it)
                            updateZhihuData(it)
                        }
                        showMessage("成功")
                    }
                    error {
                        showMessage(it.message?:"异常")
                    }
                }

    }

    private fun updateZhihuData(data:ZhihuDaily){
        tvResponse.text = data.date
        recycler.visibility = View.VISIBLE
    }

    private fun testRxGet(){
        SmartHttp.get()
                .url("http://news-at.zhihu.com/api/4/news/latest")
                .asObservable<ZhihuDaily>()
                .applySchedulers()
                ._subscribe {
                    _onNext {
                        showData(it)
                        showMessage("成功")
                    }
                    _onError {
                        it.DEBUG("失败")
                    }
                }

    }

    fun showData(data:ZhihuDaily){
        viewModel.data.value = data.date
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
        val productId: Int,        //产品id
        val productName: String,   //产品名
        val level2: Int,
        val level3: Int,           //  一垛有多少箱
        val level4: Int
)