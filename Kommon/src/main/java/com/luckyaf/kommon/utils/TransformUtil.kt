package com.luckyaf.kommon.utils

import android.util.Base64
import java.io.*

/**
 * 类描述：serialize 类转化
 * @author Created by luckyAF on 2019-02-25
 *
 */
object TransformUtil {
    @Throws(IOException::class)
    fun<A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
                byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        val string64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return string64
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    fun<A> deSerialization(str: String?): A? {
        str ?: return null
        val byteArrayInputStream = ByteArrayInputStream(Base64.decode(str, Base64.DEFAULT))
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }
}