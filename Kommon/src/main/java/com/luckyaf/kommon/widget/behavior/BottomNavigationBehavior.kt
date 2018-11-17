package com.luckyaf.kommon.widget.behavior

import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/1
 *
 */
class BottomNavigationBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {
    private var outAnimator: ObjectAnimator? = null
    private var inAnimator: ObjectAnimator? = null
    
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) {
            // 上滑隐藏
            if (outAnimator == null) {
                outAnimator = ObjectAnimator.ofFloat(child, "translationY", 0.0f, child.height.toFloat())
                outAnimator!!.duration = 200
            }
            if (!outAnimator!!.isRunning && child.translationY <= 0) {
                outAnimator!!.start()
            }
        } else if (dy < 0) {
            // 下滑显示
            if (inAnimator == null) {
                inAnimator = ObjectAnimator.ofFloat(child, "translationY", child.height.toFloat(), 0.0f)
                inAnimator!!.duration = 200
            }
            if (!inAnimator!!.isRunning && child.translationY >= child.height) {
                inAnimator!!.start()
            }
        }
    }

}