package com.cxm.lib.retrofit

import com.zhy.http.okhttp.OkHttpUtils
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit工具類
 * 陈小默 16/8/29.
 */
class RetrofitUtils {
    companion object {
        @JvmStatic fun <T> getService(baseUrl: String, service: Class<T>): T {
            val retrofit = Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
            return retrofit.create(service)
        }
    }
}
