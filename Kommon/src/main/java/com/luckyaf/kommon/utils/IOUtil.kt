package com.luckyaf.kommon.utils

import java.io.Closeable

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */

object IOUtil{
    fun close(vararg closeables: Closeable){
        closeables.iterator().forEach {
            try {
                it.close()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }
}