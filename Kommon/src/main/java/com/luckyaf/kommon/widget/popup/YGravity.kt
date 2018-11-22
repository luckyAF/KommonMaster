package com.luckyaf.kommon.widget.popup
import android.support.annotation.IntDef

/**
 * 类描述：
 * @author Created by luckyAF on 2018/11/22
 *
 */
@IntDef(YGravity.CENTER, YGravity.ABOVE, YGravity.BELOW, YGravity.ALIGN_TOP, YGravity.ALIGN_BOTTOM)
@Retention(AnnotationRetention.SOURCE)
annotation class YGravity {
    companion object {
       const val CENTER = 0
        const val ABOVE = 1
        const val BELOW = 2
        const val ALIGN_TOP = 3
        const  val ALIGN_BOTTOM = 4
    }
}
