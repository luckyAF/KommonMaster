package com.luckyaf.kommon.view.circle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.luckyaf.kommon.R

/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-27
 *
 */
class CircleView :View{
    private var centerY: Int = 0
    private var centerX: Int = 0
    private var outerRadius: Int = 0
    private var circlePaint: Paint? = null
    private var defaultColor = Color.GRAY

    constructor(context: Context):super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int):super(context, attrs, defStyle){
        init(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        circlePaint?.let {
            canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), outerRadius.toFloat(), it)
        }
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2
        outerRadius = Math.min(w, h) / 2
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint?.style = Paint.Style.FILL

        var color = defaultColor
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleView)
            color = a.getColor(R.styleable.CircleView_background, color)
            a.recycle()
        }

        setColor(color)
    }

    override fun setBackgroundColor(color: Int) {
        setColor(color)
    }

    fun setColor(color: Int) {
        this.defaultColor = color
        circlePaint?.color = defaultColor
        this.invalidate()
    }

}