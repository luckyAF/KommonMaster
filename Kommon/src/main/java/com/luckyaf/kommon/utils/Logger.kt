package com.luckyaf.kommon.utils

import android.util.Log
import com.luckyaf.kommon.BuildConfig
import com.luckyaf.kommon.extension.isMemberOf
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 类描述： 日志
 * @author Created by luckyAF on 2018/12/3
 *
 */
@Suppress("unused")
object Logger {

    private const val TOP_LEFT_CORNER = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER = '╟'
    private const val VERTICAL_DOUBLE_LINE = '║'
    private const val HORIZONTAL_DOUBLE_LINE = "═════════════════════════════════════════════════"
    private const val SINGLE_LINE = "─────────────────────────────────────────────────"
    private val TOP_BORDER = TOP_LEFT_CORNER + HORIZONTAL_DOUBLE_LINE + HORIZONTAL_DOUBLE_LINE
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + HORIZONTAL_DOUBLE_LINE + HORIZONTAL_DOUBLE_LINE
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_LINE + SINGLE_LINE

    private const val TAG = BuildConfig.APPLICATION_ID
    private var debug = BuildConfig.DEBUG//是否打印log
    private var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private val logMethod = Arrays.asList(
            "verbose", "debug",
            "info", "warn",
            "error", "assert", "v", "d", "i", "w", "e", "a"
    )

    fun v(msg: String, tag: String = TAG) = debug.debugLog(tag, msg, Log.VERBOSE)
    fun d(msg: String, tag: String = TAG) = debug.debugLog(tag, msg, Log.DEBUG)
    fun i(msg: String, tag: String = TAG) = debug.debugLog(tag, msg, Log.INFO)
    fun w(msg: String, tag: String = TAG) = debug.debugLog(tag, msg, Log.WARN)
    fun e(msg: String, tag: String = TAG) = debug.debugLog(tag, msg, Log.ERROR)


    private fun targetStackTraceMSg(): String {
        val targetStackTraceElement = getTargetStackTraceElement()
        return if (targetStackTraceElement != null) {
            "${targetStackTraceElement.className}.${targetStackTraceElement.methodName}(${targetStackTraceElement.fileName}:${targetStackTraceElement.lineNumber})"
        } else {
            ""
        }
    }

    private fun getTargetStackTraceElement(): StackTraceElement? {
        var targetStackTrace: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogClass = stackTraceElement.className == Logger::class.java.name
            System.out.println(stackTraceElement.className + " " + stackTraceElement.methodName)
            val isLogMethod = stackTraceElement.methodName.toLowerCase().isMemberOf(logMethod)
            val isLog = isLogClass || isLogMethod
            if (shouldTrace && !isLog) {
                targetStackTrace = stackTraceElement
                break
            }
            shouldTrace = isLog
        }
        return targetStackTrace
    }


    private fun Boolean.debugLog(tag: String, msg: String, priority: Int) {
        if (!this) {
            return
        }
        Log.println(priority, tag, msgFormat(msg))

    }

    private fun msgFormat(msg: String): String {
        var showMsg = formatJson(msg)
        if (showMsg.contains("\n")) {
            showMsg = showMsg.replace("\n".toRegex(), "\n$VERTICAL_DOUBLE_LINE")
        }
        return StringBuilder()
                .append("  \n")
                .append(TOP_BORDER)
                .appendln()
                .append(VERTICAL_DOUBLE_LINE)
                .append("Thread: ${Thread.currentThread().name} at ${timeFormat.format(Date())}")
                .appendln()
                .append(MIDDLE_BORDER)
                .appendln()
                .append(VERTICAL_DOUBLE_LINE)
                .append(targetStackTraceMSg())
                .appendln()
                .append(MIDDLE_BORDER)
                .appendln()
                .append(VERTICAL_DOUBLE_LINE)
                .append(showMsg)
                .appendln()
                .append(BOTTOM_BORDER)
                .toString()
    }


    /**
     * 格式化json
     * @param json
     */
    private fun formatJson(json: String): String {
        return try {
            val trimJson = json.trim()
            when {
                trimJson.startsWith("{") -> JSONObject(trimJson).toString(4)
                trimJson.startsWith("[") -> JSONArray(trimJson).toString(4)
                else -> trimJson
            }
        } catch (e: JSONException) {
            json
        }
    }

    /**
     * 是否打印log输出
     * @param debug
     */
    fun debug(debug: Boolean): Logger {
        Logger.debug = debug
        return this
    }

}
