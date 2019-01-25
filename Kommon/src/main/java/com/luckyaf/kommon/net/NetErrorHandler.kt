package com.luckyaf.kommon.net


import com.google.gson.JsonParseException
import com.luckyaf.kommon.utils.Logger
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/24
 *
 */
@Suppress("unused")
object NetErrorHandler {

    var errorCode = ErrorStatus.UNKNOWN_ERROR
    var errorMsg = "请求失败，请稍后重试"

    public fun handle(e: Throwable): ApiException {
        e.printStackTrace()
        if (e is SocketTimeoutException) {//网络超时
            Logger.e( "网络连接异常: " + e.message)
            errorMsg = "网络连接超时"
            errorCode = ErrorStatus.NETWORK_ERROR
        } else if (e is ConnectException) { //均视为网络错误
            Logger.e( "网络连接异常: " + e.message)
            errorMsg = "网络连接异常"
            errorCode = ErrorStatus.NETWORK_ERROR
        } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException
        ) {   //均视为解析错误
            Logger.e("数据解析异常: " + e.message)
            errorMsg = "数据解析异常"
            errorCode = ErrorStatus.SERVER_ERROR
        } else if (e is ApiException) {//服务器返回的错误信息
            errorMsg = e.errorMsg
            errorCode = e.errorCode
        } else if (e is UnknownHostException) {
            Logger.e( "网络连接异常: " + e.message)
            errorMsg = "网络连接异常"
            errorCode = ErrorStatus.NETWORK_ERROR
        } else if (e is IllegalArgumentException) {
            errorMsg = "好像发生了点错误哦~"
            errorCode = ErrorStatus.SERVER_ERROR
        } else {//未知错误
            errorMsg = "服务器可能抛锚了~"
            errorCode = ErrorStatus.UNKNOWN_ERROR
        }
        return ApiException(errorCode, errorMsg)

    }
}

//除了接口返回错误码之外，本地补充码
object ErrorStatus {

    /**
     * 未知错误
     */
    const val UNKNOWN_ERROR = 1600

    /**
     * 服务器内部错误
     */
    const val SERVER_ERROR = 1500

    /**
     * 网络连接超时，倾向于客户端网络问题
     */
    const val NETWORK_ERROR = 1400
}