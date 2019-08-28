package com.luckyaf.kommon.http.internal

import com.google.gson.reflect.TypeToken
import com.luckyaf.kommon.utils.GsonUtil
import okhttp3.Response

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-03
 *
 */
 class DefaultParser<T> :Parser<T>{
     override fun onParse(response: Response): T {
        return GsonUtil.provideGson().fromJson(
                response.body!!.string(),
                object : TypeToken<T>() {}.type
        )
    }

}