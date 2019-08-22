package com.luckyaf.kommon.http.request

import com.luckyaf.kommon.http.internal.HttpMethod
import java.util.*

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-05
 *
 */
class UploadRequest (
    @HttpMethod
    var method: String = HttpMethod.GET,
    private var mTag: Any? = null,
    private var mUrl: String = "",
    private var mHeaders: LinkedList<Pair<String, String>> = LinkedList(),
    private var mParams: LinkedList<Pair<String, Any>> = LinkedList()
)