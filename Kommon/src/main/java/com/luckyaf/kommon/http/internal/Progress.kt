package com.luckyaf.kommon.http.internal

/**
 * 类描述：进度
 * @author Created by luckyAF on 2019-08-05
 *
 */
data class Progress(
    /**
     * 速度
     */
    private val speed: Long = 0,
    /**
     * 当前size
     */
    private val currentSize: Long = 0,
    /**
     * 总size
     */
    private val totalSize: Long = 0,
    /**
     * 进度 0-1
     */
    private val fraction: Float = 0.0f
)