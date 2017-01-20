package com.cxm.lib.rx

import android.util.Log
import rx.Subscriber

/**
 * 通用的网络请求观察者,当网络请求出现异常时会抛出
 * 陈小默 16/9/1.
 */
abstract class NetSubscriber<T> : Subscriber<T>() {
    override fun onCompleted() {
    }

    override fun onError(e: Throwable?) {
        Log.e("---net-error---",e?.message)
    }
}