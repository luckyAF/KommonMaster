package com.luckyaf.kommon.utils

import android.util.Log
import com.luckyaf.kommon.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
class LogUtil {
    companion object {
        private var IS_DEBUG : Boolean = BuildConfig.DEBUG
        private val TAG = BuildConfig.APPLICATION_ID
        private val TOP_BORDER = "********************************************************************************************************************************************"
        private var SIDE_BORDER = "*"
        private val BOTTOM_BORDER = "********************************************************************************************************************************************"

        /**         * log         */
        fun v(tag: String, msg: String) = IS_DEBUG.log(tag, msg, Log.VERBOSE)
        fun d(tag: String, msg: String) = IS_DEBUG.log(tag, msg, Log.DEBUG)
        fun i(tag: String, msg: String) = IS_DEBUG.log(tag, msg, Log.INFO)
        fun w(tag: String, msg: String) = IS_DEBUG.log(tag, msg, Log.WARN)
        fun e(tag: String, msg: String) = IS_DEBUG.log(tag, msg, Log.ERROR)

        /**         * log         */
        fun vv(tag: String, msg: String) = IS_DEBUG.debugLog(tag, msg, Log.VERBOSE)
        fun dd(tag: String, msg: String) = IS_DEBUG.debugLog(tag, msg, Log.DEBUG)
        fun ii(tag: String, msg: String) = IS_DEBUG.debugLog(tag, msg, Log.INFO)
        fun ww(tag: String, msg: String) = IS_DEBUG.debugLog(tag, msg, Log.WARN)
        fun ee(tag: String, msg: String) = IS_DEBUG.debugLog(tag, msg, Log.ERROR)

        private fun Boolean.log(tag:String,msg:String,type:Int){
            if(!this){
                return
            }
            val newMsg = "$tag:$msg"
            when(type){
                Log.VERBOSE -> Log.v(TAG,newMsg)
                Log.DEBUG -> Log.d(TAG,newMsg)
                Log.INFO -> Log.i(TAG,newMsg)
                Log.WARN -> Log.w(TAG,newMsg)
                Log.ERROR -> Log.e(TAG,newMsg)
            }

        }
        private fun Boolean.debugLog(tag:String,msg:String,type:Int){
            if(!this){
                return
            }
            val newMsg = msgFormat(tag,msg)
            when(type){
                Log.VERBOSE -> Log.v(TAG,newMsg)
                Log.DEBUG -> Log.d(TAG,newMsg)
                Log.INFO -> Log.i(TAG,newMsg)
                Log.WARN -> Log.w(TAG,newMsg)
                Log.ERROR -> Log.e(TAG,newMsg)
            }

        }
        private fun msgFormat(tag: String,msg:String):String{
            val bytes : ByteArray = msg.toByteArray()
            val length = bytes.size
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            if (length > 100 ){
                SIDE_BORDER = ""
            }
            var newMsg = " \n$TOP_BORDER\n$SIDE_BORDER TAG: $tag\t TIME: ${sdf.format(Date())}"
            newMsg += "\n$SIDE_BORDER MSG: $msg"
            newMsg += "\n$BOTTOM_BORDER"
            return newMsg
        }
    }






}