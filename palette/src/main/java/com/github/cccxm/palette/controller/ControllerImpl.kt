package com.github.cccxm.palette.controller

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import com.github.cccxm.palette.view.BoardView
import com.github.cccxm.palette.view.layer.*
import com.github.cccxm.palette.view.shape.Shape
import com.github.cccxm.palette.view.shape.ShapeWrapper


/**
 *  project:WhiteBoard
 *  Copyright (C) <2016>  <陈小默>
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
 *  Created by 陈小默 on 2016/12/29.
 *
 *  画图板控制器实现类
 */
class ControllerImpl(context: Context) : Controller {

    /**
     * 图形对象包装对象，其中封装了一个真实的Shape对象。
     */
    private val mShape = ShapeWrapper()
    /**
     * 图片容器
     */
    private val mBitmap = BitmapCacheLayer.BitmapContainer()
    /**
     * 写字板控制器对象
     */
    private var mWordPad: WordPadImpl? = null
    /**
     * 整个画板使用的全局画笔对象
     */
    val mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            .let {
                it.color = Color.BLACK
                it.style = Paint.Style.STROKE
                it.strokeWidth = 1F
                it.strokeJoin = Paint.Join.ROUND
                it.strokeCap = Paint.Cap.ROUND
                it
            }
    /**
     * 画板的View对象
     */
    private val mView = BoardView(context, mPaint)

    /**
     * 设置是否允许多点触控
     */
    override var multiTouch = true
        set(value) {
            mView.setMultiTouch(value)
            field = value
        }

    override fun setStrokeWidth(width: Float) {
        mPaint.strokeWidth = width
    }

    override fun setStrokeColor(color: Int) {
        mPaint.color = color
    }

    override fun setBackgroundColor(color: Int) {
        mView.setBackgroundColor(color)
    }

    /**
     * 当命令重复时不操作，否则先取消上一个命令，再操作新命令
     */
    override fun setCommand(command: Controller.Command) {
        if (mView.command != command) {
            when (mView.command) {
                Controller.Command.DISABLE -> {
                }
                Controller.Command.DRAW -> {
                    mView.setCacheLayer(null)
                }
                Controller.Command.ERASER -> {
                    mPaint.xfermode = null
                }
                Controller.Command.SHAPE -> {
                    mView.setCacheLayer(null)
                }
                Controller.Command.TEXT -> {
                    mWordPad = null
                    mView.setCacheLayer(null)
                }
                Controller.Command.EXPLODE -> {
                    mView.setCacheLayer(null)
                    mView.invalidate()
                }
                Controller.Command.BITMAP -> {
                    mView.setCacheLayer(null)
                }
            }
            when (command) {
                Controller.Command.DISABLE -> {
                }
                Controller.Command.DRAW -> {
                    val layer = DrawCacheLayer(mView.getFormat(), width(), height(), mPaint)
                    mView.setCacheLayer(layer)
                }
                Controller.Command.ERASER -> {
                    mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
                Controller.Command.SHAPE -> {
                    val layer = ShapeCacheLayer(mShape, mView.getFormat(), width(), height(), mPaint)
                    mView.setCacheLayer(layer)
                }
                Controller.Command.TEXT -> {
                    if (mWordPad != null) {
                        val layer = TextCacheLayer(mView, mView.getFormat(), width(), height())
                        mWordPad!!.setLayer(layer)
                        layer.setOnDestroyListener { mWordPad == null }
                        mView.setCacheLayer(layer)
                    }
                }
                Controller.Command.EXPLODE -> {
                    val layer = ExplodeCacheLayer(mView.getFormat(), width(), height())
                    mView.setCacheLayer(layer)
                    mView.explode()
                }
                Controller.Command.BITMAP -> {
                    val layer = BitmapCacheLayer(mBitmap, mView.getFormat(), width(), height())
                    mView.setCacheLayer(layer)
                }
            }
        }
        mView.command = command
    }

    override fun drawShape(shape: Shape) {
        mShape.shape = shape
    }

    override fun drawText(): WordPad {
        if (mWordPad == null)
            mWordPad = WordPadImpl()
        return mWordPad!!
    }

    override fun drawBitmap(bitmap: Bitmap) {
        mBitmap.bitmap = bitmap
    }

    override fun undo() {
        if (hasUndo())
            mView.undo()
    }

    override fun redo() {
        if (hasRedo())
            mView.redo()
    }

    override fun hasUndo(): Boolean {
        return mView.hasUndo()
    }

    override fun hasRedo(): Boolean {
        return mView.hasRedo()
    }

    override fun width(): Int {
        return mView.width
    }

    override fun height(): Int {
        return mView.height
    }

    override fun clear() {
        mView.clear()
    }

    override fun toPicture(): Bitmap {
        return mView.toPicture()
    }

    override fun getView(): SurfaceView {
        return mView
    }
}