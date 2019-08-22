package com.luckyaf.kommon.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader


/**
 * 类描述：
 * @author Created by luckyAF on 2019-02-18
 *
 */
@Suppress("unused")
object ShellUtils {
    private val LINE_SEP = System.getProperty("line.separator")


    fun execCmd(command: String, isRooted: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRooted, true)
    }

    fun execCmd(commands: List<String>?, isRooted: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(), isRooted, true)
    }

    fun execCmd(commands: Array<String>, isRooted: Boolean): CommandResult {
        return execCmd(commands, isRooted, true)
    }


    fun execCmd(command: String,
                isRooted: Boolean,
                isNeedResultMsg: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRooted, isNeedResultMsg)
    }


    fun execCmd(commands: List<String>?,
                isRooted: Boolean,
                isNeedResultMsg: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(),
                isRooted,
                isNeedResultMsg)
    }


    fun execCmd(commands: Array<String>?,
                isRooted: Boolean,
                isNeedResultMsg: Boolean): CommandResult {
        var result = -1
        if (commands == null || commands.isEmpty()) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if(command.isNullOrEmpty()){
                    continue
                }
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                        InputStreamReader(process.inputStream, "UTF-8")
                )
                errorResult = BufferedReader(
                        InputStreamReader(process.errorStream, "UTF-8")
                )
                var line = successResult.readLine()
                if (line != null) {
                    successMsg.append(line)
                    line = successResult.readLine()
                    while (line != null) {
                        successMsg.append(LINE_SEP).append(line)
                        line = successResult.readLine()
                    }
                }
                line = errorResult.readLine()
                if (line != null) {
                    errorMsg.append(line)
                      line = errorResult.readLine()
                    while (line  != null) {
                        errorMsg.append(LINE_SEP).append(line)
                        line = errorResult.readLine()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                successResult?.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {

                errorResult?.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            process?.destroy()
        }
        return CommandResult(
                result,
                successMsg?.toString() ?: "",
                errorMsg?.toString() ?: ""
        )
    }

    /**
     * The result of command.
     */
    class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {

        override fun toString(): String {
            return "result: " + result + "\n" +
                    "successMsg: " + successMsg + "\n" +
                    "errorMsg: " + errorMsg
        }
    }
}