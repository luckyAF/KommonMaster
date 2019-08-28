package com.luckyaf.kommon.mvi

import com.luckyaf.kommon.base.BaseView

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-28
 *
 */
interface IntentView : BaseView {
    /**
     * 提供可测试的intent
     */
    fun provideIntents():Map<String,IIntent>{
        return emptyMap()
    }
    /**
     * 处理intent(测试时使用)
     */
    fun processor(intent:IIntent){}

    /**
     * 根据ViewState 渲染 页面
     */
    fun render(state:IViewState)

    /**
     * 处理UI事件
     * 比如跳转Activity,显示dialog,
     */
    fun handleUiEvent(event:CommonUIEvent)
}