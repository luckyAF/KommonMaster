package com.luckyaf.kommon.sample

import com.luckyaf.kommon.callback._subscribe
import com.luckyaf.kommon.extension.addToComposite
import com.luckyaf.kommon.extension.executeIO
import io.reactivex.Observable

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


    fun runSubscribe(){
        val arrayData = listOf(0..20)
        Observable.fromArray(arrayData)._subscribe{
            _onSubscribe {

            }
            _onComplete {

            }
            _onError {

            }
            _onNext {

            }
        }
    }


}