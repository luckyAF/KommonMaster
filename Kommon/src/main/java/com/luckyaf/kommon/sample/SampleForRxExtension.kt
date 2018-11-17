package com.luckyaf.kommon.sample

import com.luckyaf.kommon.extension.addToComposite
import com.luckyaf.kommon.extension.executeIO

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/10
 *
 */
class Test {
    fun runOnIO() {
        executeIO(
                {
                    "test"  // 耗时操作返回数据  例如  getCache
                },
                {
                    print(it) //   操作数据
                }
        ).addToComposite(null)

    }
}