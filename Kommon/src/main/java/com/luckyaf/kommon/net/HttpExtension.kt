package com.luckyaf.kommon.net

import com.luckyaf.kommon.extension.toJavaBean
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
fun String.http(tag: Any = this): CommonRequest {
    return CommonRequest(tag, url = this)
}

/**
 * get请求，需在协程中使用。结果为空即为失败，并会将失败信息打印日志。
 */
inline fun <reified T> CommonRequest.execute(): Deferred<T?> {
    return doRequest(this)
}

inline  fun <reified T> CommonRequest.execute(callback: HttpCallback<T>) {
     callbackRequest(this,callback)
}


inline fun <reified T> doRequest(request: CommonRequest): Deferred<T?> {
    val req = request.buildRequest()
    val call = NetManager.mOkHttpClient.newCall(req)
            .apply { NetManager.requestCache[request.tag()] = this } //cache req
    val deferred = CompletableDeferred<T?>()
    deferred.invokeOnCompletion {
        if (deferred.isCancelled)
            call.cancel()
    }
    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            NetManager.requestCache.remove(request.tag())
//            deferred.completeExceptionally(e)
            deferred.complete(null) //pass null
            e.printStackTrace()
        }
        override fun onResponse(call: Call, response: Response) {
            NetManager.requestCache.remove(request.tag())
            if (response.isSuccessful) {
                if ("String type" is T) {
                    deferred.complete(response.body()!!.string() as T)
                } else {
                    deferred.complete(response.body()!!.string().toJavaBean<T>())
                }
            } else {
                //not throw
//              deferred.completeExceptionally(IOException(response))
                onFailure(call, IOException("request to ${request.url()} is fail; http code: ${response.code()}!"))
            }
        }
    })
    return deferred
}

inline fun <reified T> callbackRequest(request: CommonRequest, callback: HttpCallback<T>) {
    val req = request.buildRequest()
    NetManager.mOkHttpClient.newCall(req).apply {
        NetManager.requestCache[request.tag()] = this //cache req
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetManager.requestCache.remove(request.tag())
                callback.onError(e)
            }
            override fun onResponse(call: Call, response: Response) {
                NetManager.requestCache.remove(request.tag())
                if (response.isSuccessful) {
                    if ("ktx" is T) {
                        callback.onSuccess(response.body()!!.string() as T)
                    } else {
                        callback.onSuccess(response.body()!!.string().toJavaBean<T>())
                    }
                } else {
                    callback.onError(IOException("request to ${request.url()} is fail; http code: ${response.code()}!"))
                }
            }
        })
    }

}
