package com.github.cccxm.palette.view.layer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.cxm.utils.Logger
import com.github.cccxm.palette.util.CanvasUtil
import com.github.cccxm.palette.view.model.Model

/**
 *  project:WhiteBoard
 *  Copyright (C) <2017>  <陈小默>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Created by 陈小默 on 2017/1/5.
 *
 *  缓冲层定义接口，缓冲层在SurfaceView具有尺寸时创建，在SurfaceView销毁时销毁
 */
abstract class CacheLayer(val format: Bitmap.Config?,
                          val width: Int,
                          val height: Int,
                          val paint: Paint) {

    /**
     * 绘图层缓冲区对象
     */
    val cacheBitmap = Bitmap.createBitmap(width, height, format)

    /**
     * 是否开启多点触摸
     */
    var isMultiTouch = true

    /**
     * 绘图画板对象
     */
    protected val cacheCanvas = Canvas().apply {
        setBitmap(cacheBitmap)
    }

    /**
     * 日志输出对象
     */
    protected val logger = Logger("Layer")

    /**
     * 当前是否正在绘制
     */
    var isDrawing = false

    protected lateinit var mClearRedo: () -> Unit
    protected lateinit var mAddHistory: (Model) -> Unit

    fun prepare(clearRedo: () -> Unit,
                addHistory: (Model) -> Unit) {
        this.mClearRedo = clearRedo
        this.mAddHistory = addHistory
    }

    private var mDestroyListener: (() -> Unit)? = null

    fun setOnDestroyListener(destroyListener: () -> Unit) {
        mDestroyListener = destroyListener
    }

    /**
     * SurfaceView销毁时调用，在此方法进行清理工作
     */
    open fun onDestroy() {
        logger.e("onDestroy")
        cacheBitmap.recycle()
        mDestroyListener?.invoke()
    }

    fun clearCache() {
        CanvasUtil.clear(cacheCanvas)
    }

    abstract fun onTouchEvent(event: MotionEvent)
}