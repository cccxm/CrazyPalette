package com.cxm.utils

import android.os.Handler
import java.util.*

/**
 * 多线程控制管理器

 * @author 陈小默
 * *
 * @create 2015.10.12
 */
object ThreadUtils {
    /**
     * 就绪列表
     */
    private val runnableMap = HashMap<String, Runnable>()
    /**
     * 运行时列表
     */
    private val threadMap = HashMap<String, MThread>()

    /**
     * 获得一个线程,线程不存在返回空
     */
    private fun getThread(threadName: String): Thread? {
        val thread = threadMap[threadName]
        if (thread != null) {
            return thread
        } else {
            val runnable = runnableMap[threadName]
            if (runnable == null) {
                return null
            } else {
                val t = MThread(threadName, runnable)
                threadMap.put(threadName, t)
                return t
            }
        }
    }

    /**
     * 向就绪表中插入一条指定名称的数据
     */
    fun build(threadName: String, runnable: (Unit) -> Unit) {
        runnableMap.put(threadName, Runnable { runnable.invoke(Unit) })
    }

    /**
     * 向就绪表中插入一条指定名称的数据
     */
    fun build(threadName: String, runnable: Runnable) {
        runnableMap.put(threadName, runnable)
    }

    /**
     * 启动一个线程
     */
    fun start(threadName: String) {
        val thread = getThread(threadName) ?: return
        thread.start()
    }

    /**
     * 从就绪表中移除数据,不影响正在运行的线程
     */
    fun remove(threadName: String) {
        if (runnableMap.containsKey(threadName))
            runnableMap.remove(threadName)
    }

    /**
     * 清空就绪表
     */
    fun clear() {
        runnableMap.clear()
    }

    /**
     * 获得某一线程的存活状态
     */
    fun isAlive(threadName: String): Boolean {
        val mThread = threadMap[threadName]
        if (mThread != null)
            return !mThread.kill
        return false
    }

    /**
     * 停止一个线程
     */
    fun stop(threadName: String) {
        val mThread = threadMap[threadName] ?: return
        mThread.kill = true
        mThread.interrupt()
    }

    /**
     * 批量杀死线程
     */
    fun stopAll() {
        val iterator = threadMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.value.keep_kill(true)
            stop(entry.key)
            iterator.remove()
        }
    }

    /**
     * 得到当前正在运行的线程总数
     */
    val run: Int
        get() = threadMap.size

    private class MThread : Thread {
        /**
         * 线程是否被杀死
         */
        var kill = false
        var keep_kill = false
        var isRunning = false
        /**
         * 正在被团灭
         */
        fun keep_kill(keep_kill: Boolean) {
            this.keep_kill = keep_kill
        }

        constructor(threadName: String, runnable: Runnable) : super(runnable) {
            this.name = threadName
        }

        override fun run() {
            try {
                super.run()
            } catch (e: InterruptedException) {
            }
            if (!keep_kill) {
                ThreadUtils.threadMap.remove(name)
            }
        }

        override fun start() {
            if (!isRunning) {
                isRunning = true
                super.start()
            }
        }
    }

    class Loop(val timer: (Int) -> Long) {
        constructor() : this(0)
        constructor(time: Long) : this({ time })

        private val threadName = RandomUtils.getString()
        private val handler = Handler()
        private var count = 0
        private var isLoop = false
        private var isStart = false
        private var isDestroy = false
        private var desCallback: ((Unit) -> Unit)? = null
        private var stopCallback: ((Int) -> Unit)? = null

        fun loop(run: (Int) -> Boolean): Loop {
            if (isLoop)
                throw RuntimeException("already loop")
            ThreadUtils.build(threadName, Runnable {
                while (ThreadUtils.isAlive(threadName)) {
                    handler.post { if (!run.invoke(count)) stop() }
                    Thread.sleep(timer.invoke(count++))
                }
            })
            isLoop = true
            return this
        }

        fun onDestroy(callback: (Unit) -> Unit): Loop {
            desCallback = callback
            return this
        }

        fun onStop(callback: (Int) -> Unit): Loop {
            stopCallback = callback
            return this
        }

        fun start(): Loop {
            if (isDestroy) throw RuntimeException("already destory")
            if (!isStart) {
                isStart = true
                count = 0
                ThreadUtils.start(threadName)
            }
            return this
        }

        fun stop() {
            ThreadUtils.stop(threadName)
            stopCallback?.invoke(count)
            isStart = false
        }

        fun destroy() {
            isDestroy = true
            ThreadUtils.remove(threadName)
            ThreadUtils.stop(threadName)
            desCallback?.invoke(Unit)
        }
    }
}