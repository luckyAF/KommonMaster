package com.luckyaf.kommon.extension

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/3
 *
 */


fun  <T:Any> T.addTo(collection: MutableCollection<T>?){
    collection?.add(this)
}


fun <T:Any> Array<T>.addAllTo(collection: MutableCollection<T>?){
    collection?.addAll(this)
}

fun <T:Any> Collection<T>.addAllTo(collection: MutableCollection<T>?){
    collection?.addAll(this)
}

fun <T:Any> T?.isMemberOf(collection: MutableCollection<T>?) : Boolean{
    collection?:return false
    return collection.contains(this)

}
