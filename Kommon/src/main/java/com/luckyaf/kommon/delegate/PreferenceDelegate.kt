package com.luckyaf.kommon.delegate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceDataStore
import android.util.Base64
import com.luckyaf.kommon.Kommon
import java.io.*
import kotlin.reflect.KProperty

/**
 * 类描述：
 * @author Created by luckyAF on 2018/10/11
 *
 */
@Suppress("UNUSED")
class PreferenceDelegate<T>(private val name:String, private val defaultValue:T) {
    companion object {
        private val file_name = "sp_" + Kommon.appName
    }
    private val prefs: SharedPreferences by lazy {
        Kommon.context.getSharedPreferences(file_name, Context.MODE_PRIVATE)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(name, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        put(name, value)
    }


    @SuppressLint("CommitPrefEdits")
    private fun put(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name,serialize(value))

        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun get(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else ->  deSerialization(getString(name,serialize(default)))
        }
        return res as T
    }

    /**
     * 删除全部数据
     */
    fun clearPreference(){
        prefs.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key : String){
        prefs.edit().remove(key).apply()
    }




    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param
     * @return
     */
    fun getAll(): Map<String, *> {
        return prefs.all
    }

}


fun <T> preferenceDelegate(name: String,defaultValue: T) = PreferenceDelegate(name,defaultValue)

fun preferenceDelegate(name: String) = ExtrasDelegate(name,null)




@Throws(IOException::class)
private fun<A> serialize(obj: A): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
    objectOutputStream.writeObject(obj)
    val string64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT)
    objectOutputStream.close()
    byteArrayOutputStream.close()
    return string64
}

@Suppress("UNCHECKED_CAST")
@Throws(IOException::class, ClassNotFoundException::class)
private fun<A> deSerialization(str: String?): A? {
    str ?: return null
    val byteArrayInputStream = ByteArrayInputStream(Base64.decode(str,Base64.DEFAULT))
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    val obj = objectInputStream.readObject() as A
    objectInputStream.close()
    byteArrayInputStream.close()
    return obj
}