package com.cxm.myutils

import android.app.Application
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.cookie.CookieJarImpl
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore
import com.zhy.http.okhttp.https.HttpsUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 默認Application類
 * 陈小默 16/8/23.
 */
abstract class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val cookieJar = CookieJarImpl(PersistentCookieStore(applicationContext))
        val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
        val okHttpClient = OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .cache(Cache(cacheDir, 1024L * 1024 * 10))
                .build()//其他配置

        OkHttpUtils.initClient(okHttpClient)
        Picasso.setSingletonInstance(Picasso.Builder(this)
                .downloader(OkHttp3Downloader(OkHttpUtils.getInstance().okHttpClient)).build())
    }
}