package com.cxm.view

/**
 * View的消息订阅者接口
 */
interface IViewSubscriber<Queue> {
    /**
     * 开始观察
     */
    fun start(queue: Queue)

    /**
     * 停止观察
     */
    fun stop()

    /**
     * 结束任务
     */
    fun finish()
}