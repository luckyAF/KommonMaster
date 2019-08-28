package com.luckyaf.kommon.mvi

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import com.luckyaf.kommon.component.SingleLiveEvent

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-28
 *
 */
abstract class BaseModel< I :IIntent, S:IViewState>: ViewModel(){

    /**
     *  页面展示数据
     *  例如 list，user Detail
     */
    private var viewState = MutableLiveData<S>()

    /**
     * 通用的UI事件
     * 例如 显示message，dialog，页面跳转
     */
    private var commonEvent  = SingleLiveEvent<CommonUIEvent>()

    /**
     * 初始化数据
     */
    abstract fun initData(bundle: Bundle)

    /**
     * 处理意图
     */
    abstract fun processor(intent:I?)

    /**
     * 初始化的State
     */
    abstract fun initializeState():S

    /**
     * 绑定到View
     */
    fun bindView(view:IntentView){
        viewState.observe(view, Observer {
            it?:return@Observer
            view.render(it)
        })
        commonEvent.observe(view, Observer {
            it ?: return@Observer
            when (it) {
                is CommonUIEvent.Message -> {
                    view.showMessage(it.message,it.messageType)
                }
                is CommonUIEvent.Loading -> {
                    if (it.show) {
                        view.showLoading(it.message)
                    } else {
                        view.hideLoading()
                    }
                }
                else -> {
                    view.handleUiEvent(it)
                }
            }
        })
        postState(state())
    }

    /**
     * 发送事件 显示loading
     */
    protected fun postShowLoading(message:String=""){
        commonEvent.value = CommonUIEvent.Loading(true,message)
    }
    /**
     * 发送事件 隐藏loading
     */
    protected fun postHideLoading(){
        commonEvent.value = CommonUIEvent.Loading(false)
    }
    /**
     * 发送事件 显示message
     */
    protected fun postShowMessage(content:String){
        commonEvent.value = CommonUIEvent.Message(content)
    }

    /**
     * 获取当前状态
     */
    protected fun state() = viewState.value ?:initializeState()

    /**
     * 更新新状态
     */
    protected fun postState(s:S?){
        viewState.value = s
    }
    /**
     * 发送事件
     */
    protected fun postEvent(event:CommonUIEvent){
        commonEvent.value = event
    }


}