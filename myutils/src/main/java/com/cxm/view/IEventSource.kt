package com.cxm.view

import android.view.MotionEvent

/**
 * 事件源接口
 * 陈小默 16/8/30.
 */
interface IEventSource<Subscriber> {
    fun register(subscriber: Subscriber)
    fun event(event: MotionEvent): Boolean
    fun unregister()
}