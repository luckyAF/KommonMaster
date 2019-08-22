package com.luckyaf.kommon.extension

/**
 * 类描述：
 * @author Created by luckyAF on 2019-08-21
 *
 */


inline fun tryCatch(tryBlock: () -> Unit,
                    catchBlock: (Exception) -> Unit = {},
                    finally :() ->Unit={}
) {
    try {
        tryBlock()
    } catch (e: Exception) {
        e.printStackTrace()
        catchBlock(e)
    }finally {
        finally()
    }
}
