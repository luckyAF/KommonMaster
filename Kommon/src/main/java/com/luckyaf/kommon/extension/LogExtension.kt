package com.luckyaf.kommon.extension

import com.luckyaf.kommon.utils.GsonUtil
import com.luckyaf.kommon.utils.Logger

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/6
 *
 */

fun < T : Any> T?.VERBOSE(tag: String = this.toString()) {
    if (this == null) {
        Logger.v("value is null", tag)
        return
    }
    Logger.v(GsonUtil.provideGson().toJson(this), tag)
}
fun < T : Any> T?.INFO(tag: String = this.toString()) {
    if (this == null) {
        Logger.i("value is null", tag)
        return
    }
    Logger.i(GsonUtil.provideGson().toJson(this), tag)
}
fun < T : Any> T?.WARN(tag: String = this.toString()) {
    if (this == null) {
        Logger.w("value is null", tag)
        return
    }
    Logger.w(GsonUtil.provideGson().toJson(this), tag)
}
fun < T : Any> T?.DEBUG(tag: String = this.toString()) {
    if (this == null) {
        Logger.d("value is null", tag)
        return
    }
    Logger.d(GsonUtil.provideGson().toJson(this), tag)
}
fun < T : Any> T?.ERROR(tag: String = this.toString()) {
    if (this == null) {
        Logger.e("value is null", tag)
        return
    }
    Logger.e(GsonUtil.provideGson().toJson(this), tag)
}

