package com.cxm.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.BounceInterpolator
import android.widget.RelativeLayout
import java.util.LinkedList

/**
 * 下拉容器
 * 陈小默 16/8/30.
 */
class DropLayout : RelativeLayout, IViewSubscriber<LinkedList<Int>> {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var isStop: Boolean = true
    var drop_top = 0
    var drop_bottom = resources.displayMetrics.heightPixels
    var fixed = false
    override fun start(queue: LinkedList<Int>) {
        isStop = false
        Thread({
            while (!isStop)
                if (queue.size > 0) {
                    val site = queue.removeFirst()
                    if (site != null) {
                        post { height = site }
                    }
                }
        }).start()
    }

    override fun stop() {
        isStop = true
    }

    override fun finish() {
        val h = height
        val screen_height = context.resources.displayMetrics.heightPixels
        if (!fixed)
            if (h > screen_height / 2) {
                moveToBottom()
            } else {
                moveToTop()
            }
    }

    fun setHeight(height: Int) {
        val param = layoutParams
        param.height = height
        layoutParams = param
    }

    private fun moveToTop() {
        ObjectAnimator.ofInt(this, "height", drop_top).setDuration(500).start()
    }

    private fun moveToBottom() {
        val animator = ObjectAnimator.ofInt(this, "height", drop_bottom).setDuration(1000)
        animator.interpolator = BounceInterpolator()
        animator.start()
    }
}