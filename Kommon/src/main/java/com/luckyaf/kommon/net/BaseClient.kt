package com.luckyaf.kommon.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 类描述：
 * @author Created by luckyAF on 2019/1/11
 *
 */
object BaseClient {

    val mOkHttpClient by lazy {
        OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    val mRetrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://www.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build()
    }
}