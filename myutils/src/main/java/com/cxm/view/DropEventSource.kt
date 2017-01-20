package com.cxm.view

import android.view.MotionEvent
import java.util.*

/**
 * 下拉容器事件源实现类
 * 陈小默 16/8/30.
 */
class DropEventSource : IEventSource<DropLayout> {
    private var subscriber: DropLayout? = null
    private val queue: LinkedList<Int> = LinkedList()
    override fun register(subscriber: DropLayout) {
        this.subscriber = subscriber
    }

    override fun event(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> start()
            MotionEvent.ACTION_MOVE -> move(event)
            MotionEvent.ACTION_UP -> stop()
        }
        return true
    }

    fun start() {
        subscriber!!.start(queue)
    }

    fun move(event: MotionEvent) {
        val height = event.y.toInt()
        queue.addLast(height)
    }

    fun stop() {
        subscriber!!.stop()
        subscriber!!.finish()
    }

    override fun unregister() {
        subscriber = null
    }
}