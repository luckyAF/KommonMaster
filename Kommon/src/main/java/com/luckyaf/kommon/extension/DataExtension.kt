package com.luckyaf.kommon.extension


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

fun <T:Any> List<T>.safeSubList(fromIndex: Int, toIndex: Int):List<T>{
    if (fromIndex > size) {
        return emptyList()
    }
    if (toIndex < 0) {
        return emptyList()
    }
    val start = if (fromIndex < 0) {
        0
    } else {
        fromIndex
    }
    val end = if (toIndex > size) {
        size
    } else {
        toIndex
    }
    return if (start > end) {
        this.subList(end, start).reversed()
    } else {
        this.subList(start, end)
    }

}
