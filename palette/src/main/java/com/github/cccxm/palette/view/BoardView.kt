package com.github.cccxm.palette.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.cccxm.palette.util.Logger
import com.github.cccxm.palette.controller.Controller
import com.github.cccxm.palette.util.CanvasUtil
import com.github.cccxm.palette.util.FormatUtil
import com.github.cccxm.palette.util.HistoryUtil
import com.github.cccxm.palette.view.layer.CacheLayer
import com.github.cccxm.palette.view.layer.DrawCacheLayer
import com.github.cccxm.palette.view.layer.ExplodeCacheLayer
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
 *  Created by 陈小默 on 2017/1/3.
 *
 * 该View既是显示图像的SurfaceView也是绘图缓冲区对象
 */
class BoardView(context: Context,
                val mPaint: Paint) : SurfaceView(context), SurfaceHolder.Callback {
    constructor(context: Context) : this(context, Paint())

    init {
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        keepScreenOn = true
    }

    /**
     * 当前View的格式
     */

    private var mFormat: Bitmap.Config? = null

    fun getFormat(): Bitmap.Config? = mFormat

    /**
     * 设置绘制命令
     */
    var command = Controller.Command.DRAW
    /**
     * 设置是否允许多点触控
     */
    private var mMultiTouch = true

    fun setMultiTouch(multiTouch: Boolean) {
        mMultiTouch = multiTouch
        mCacheLayer?.isMultiTouch = multiTouch
    }

    private val logger = Logger(this)

    /**
     * 最底层的缓冲区以及绘制对象
     */
    private lateinit var cacheBitmap: Bitmap
    private lateinit var cacheCanvas: Canvas
    private lateinit var mHistory: HistoryUtil

    /**
     * 缓冲绘图层
     */
    private var mCacheLayer: CacheLayer? = null


    private var CLEAR_REDO_STACK: () -> Unit = {
        mHistory.clearRedoStack()
    }
    private var ADD_HISTORY: (Model) -> Unit = {
        mHistory.addHistory(it)
    }

    /**
     * 设置当前的扩展层对象，如果为空表示取消扩展层
     */
    fun setCacheLayer(cacheLayer: CacheLayer?) {
        if (cacheLayer != null) {
            cacheLayer.prepare(CLEAR_REDO_STACK, ADD_HISTORY)
        } else if (mCacheLayer != null) {
            mCacheLayer?.onDestroy()
        }
        mCacheLayer = cacheLayer
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        logger.e("surface changed , width = $width , height = $height")
        mFormat = FormatUtil.getConfig(format)
        cacheBitmap = Bitmap.createBitmap(width, height, mFormat)
        cacheCanvas = Canvas()
        cacheCanvas.setBitmap(cacheBitmap)
        mHistory = HistoryUtil(cacheCanvas)
        setCacheLayer(DrawCacheLayer(mFormat, width, height, mPaint))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        logger.e("surface destroyed")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        logger.e("surface created")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (command) {
            Controller.Command.DISABLE -> return true
            Controller.Command.ERASER -> {
                eraser(event)
            }
            else -> mCacheLayer?.onTouchEvent(event)
        }
        invalidate()
        return true
    }

    private var mEraserPath = Path()
    private var mEraserX = 0f
    private var mEraserY = 0f
    /**
     * 由于橡皮擦必须直接作用于底层缓冲区，所以需要放在View中
     */
    private fun eraser(event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mEraserPath.reset()
                mEraserPath.moveTo(x, y)
                mEraserX = x
                mEraserY = y
                CLEAR_REDO_STACK()
            }
            MotionEvent.ACTION_UP -> {
                ADD_HISTORY(HistoryUtil.toHistory(CanvasUtil.paintCopy(mPaint), CanvasUtil.pathCopy(mEraserPath)))
            }
            MotionEvent.ACTION_MOVE -> {
                if (Math.abs(x - mEraserX) > 3 || Math.abs(y - mEraserY) > 3) {
                    mEraserPath.quadTo(mEraserX, mEraserY, (x + mEraserX) / 2, (y + mEraserY) / 2)
                    mEraserX = x
                    mEraserY = y
                    cacheCanvas.drawPath(mEraserPath, mPaint)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(cacheBitmap, 0f, 0f, null)
        val layer = mCacheLayer
        if (layer != null && layer.isDrawing) {
            canvas.drawBitmap(layer.cacheBitmap, 0f, 0f, null)
        }
    }

    fun explode() {
        val layer = mCacheLayer
        if (layer != null && layer is ExplodeCacheLayer) {
            layer.onCreate(mHistory)
        }
        invalidate()
    }

    /**
     * 先清空栈，再清空屏幕上的内容
     */
    fun clear() {
        logger.e("clear")
        mHistory.clear()
        invalidate()
    }

    /**
     * 当undo栈不为空时，出栈一个数据存储到redo栈中，然后清空屏幕并重绘
     */
    fun undo() {
        logger.e("undo")
        mHistory.undo()
        invalidate()
    }

    /**
     * 当redo栈不为空时，出栈一个数据绘制到屏幕上，并将数据入栈到undo栈中。
     */
    fun redo() {
        logger.e("redo")
        mHistory.redo()
        invalidate()
    }

    fun hasUndo() = mHistory.hasUndo()
    fun hasRedo() = mHistory.hasRedo()

    fun toPicture(): Bitmap {
        logger.e("to picture")
        return Bitmap.createBitmap(cacheBitmap)
    }
}