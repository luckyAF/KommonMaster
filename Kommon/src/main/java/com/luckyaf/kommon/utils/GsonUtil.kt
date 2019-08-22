package com.luckyaf.kommon.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Reader
import java.lang.reflect.Type

/**
 * 类描述：
 * @author Created by luckyAF on 2018/12/6
 *
 */
object GsonUtil {

    private val GSON = createGson(true)

    private val GSON_NO_NULLS = createGson(false)

    /**
     * Gets pre-configured [Gson] instance.
     *
     * @return [Gson] instance.
     */
    val gson: Gson
        get() = getGson(true)

    /**
     * Gets pre-configured [Gson] instance.
     *
     * @return [Gson] instance.
     */
    fun provideGson(): Gson {
        return getGson(true)
    }

    /**
     * Gets pre-configured [Gson] instance.
     *
     * @param serializeNulls determines if nulls will be serialized.
     * @return [Gson] instance.
     */
    fun getGson(serializeNulls: Boolean): Gson {
        return if (serializeNulls) GSON_NO_NULLS else GSON
    }

    /**
     * Serializes an object into json.
     *
     * @param object       the object to serialize.
     * @param includeNulls determines if nulls will be included.
     * @return object serialized into json.
     */
    @JvmOverloads
    fun toJson(`object`: Any, includeNulls: Boolean = true): String {
        return if (includeNulls) GSON.toJson(`object`) else GSON_NO_NULLS.toJson(`object`)
    }


    /**
     * Converts [String] to given type.
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(json: String, type: Class<T>): T {
        return GSON.fromJson(json, type)
    }

    /**
     * Converts [String] to given type.
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(json: String, type: Type): T {
        return GSON.fromJson(json, type)
    }

    /**
     * Converts [Reader] to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(reader: Reader, type: Class<T>): T {
        return GSON.fromJson(reader, type)
    }

    /**
     * Converts [Reader] to given type.
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(reader: Reader, type: Type): T {
        return GSON.fromJson(reader, type)
    }


    /**
     * Create a pre-configured [Gson] instance.
     *
     * @param serializeNulls determines if nulls will be serialized.
     * @return [Gson] instance.
     */
    private fun createGson(serializeNulls: Boolean): Gson {
        val builder = GsonBuilder()
        if (serializeNulls) {
            builder.serializeNulls()
        }
        return builder.create()
    }


}
