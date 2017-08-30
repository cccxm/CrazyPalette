package com.github.cccxm.palette.view.model

import android.graphics.*
import java.util.*
import java.lang.Math.sin
import java.lang.Math.cos

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
 *  Created by 陈小默 on 2017/1/12.
 */
abstract class Model {
    companion object {
        var explode = false
    }

    private val mFramePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 1F
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = DashPathEffect(floatArrayOf(1f, 1f, 3f, 3f), 0f)
    }
    private val mFramePath = Path()
    protected var frameBounds: RectF = RectF()
    /**
     * 用于记录变换历史
     */
    private var mHistory = History()

    private var startX = 0f
    private var startY = 0f

    protected val mUndoStack = Stack<History>().apply {
        push(mHistory.copy())
    }
    protected val mRedoStack = Stack<History>()

    /**
     * 扩宽边框
     */
    protected fun RectF.addWidth(width: Float) {
        this.top -= width
        this.left -= width
        this.bottom += width
        this.right += width
    }

    /**
     * 移动边框
     */
    protected fun RectF.move(absX: Float, absY: Float) {
        this.top += absY
        this.left += absX
        this.bottom += absY
        this.right += absX
    }

    /**
     * 缩放边框
     */
    protected fun RectF.scale(scale: Float, centerX: Float = 0f, centerY: Float = 0f) {
        top = (top - centerY) * scale + centerY
        left = (left - centerX) * scale + centerX
        bottom = (bottom - centerY) * scale + centerY
        right = (right - centerX) * scale + centerX
    }

    /**
     * 旋转边框
     */
    protected fun RectF.rotate(rotate: Float, centerX: Float = 0f, centerY: Float = 0f) {
        val sinRotate = sin(rotate.toDouble()).toFloat()
        val cosRotate = cos(rotate.toDouble()).toFloat()
        val newHeight = height() * sinRotate + width() * cosRotate
        val newWidth = height() * cosRotate + width() * sinRotate
        right = left + newWidth
        bottom = top + newHeight
        val x2 = (centerX - left) * cosRotate + (centerY - bottom) * sinRotate + left
        val y2 = (centerY - bottom) * cosRotate - (centerX - left) * sinRotate + bottom
        move(centerX - x2, centerY - y2)
    }

    /**
     * 扩展函数：判断当前点坐标是否落在RectF区域内
     */
    private fun RectF.inner(x: Float, y: Float): Boolean =
            x > left && x < right && y > top && y < bottom

    /**
     * 判断坐标是否落在自身的RectF内
     */
    fun dispatcher(x: Float, y: Float): Boolean = frameBounds.inner(x, y)

    fun hasUndo(): Boolean = mUndoStack.size > 1

    fun hasRedo(): Boolean = mRedoStack.size != 0

    fun undo() {
        val his = mUndoStack.pop()
        mRedoStack.push(his)
        mHistory = mUndoStack.peek().copy()
    }

    fun redo() {
        val his = mRedoStack.pop()
        mUndoStack.push(his)
        mHistory = his.copy()
    }

    /**
     * 通知Model保存当前状态
     */
    fun save() {
        mUndoStack.push(mHistory.copy())
        mRedoStack.clear()
    }

    protected abstract fun drawHistory(canvas: Canvas, history: History, isBounds: Boolean): Boolean

    /**
     * 绘制Model
     */
    fun draw(canvas: Canvas) {
        if (explode) {
            val isChange = drawHistory(canvas, mHistory, true)
            if (isChange)
                drawFrame(canvas, frameBounds)
        } else
            drawHistory(canvas, mHistory, false)
    }

    /**
     * 设置起始位置
     */
    fun startTo(x: Float, y: Float) {
        startX = x
        startY = y
    }

    /**
     * 平移当前Model移动到的位置
     */
    fun moveTo(x: Float, y: Float) {
        mHistory.centerX = x
        mHistory.centerY = y
        mHistory.isMove = true
        mHistory.x = x - startX
        mHistory.y = y - startY
        val top = mUndoStack.peek()
        mHistory.x += top.x
        mHistory.y += top.y
    }


    /**
     * 旋转当前Model，需要传入角度而不是弧度，需要在draw方法前调用
     */
    fun rotate(angle: Float) {
        mHistory.isRotate = true
        mHistory.rotate = angle + mUndoStack.peek().rotate
    }


    private val MIN_SCALE = 0.5f
    private val MAX_SCALE = 2F
    /**
     * 缩放比例
     *
     * @param scale 取址范围 0.5 - 2
     */
    fun scale(scale: Float) {
        var mScale = scale
        mHistory.isScale = true
        mScale *= mUndoStack.peek().scale
        if (mScale < MIN_SCALE)
            mHistory.scale = MIN_SCALE
        else if (mScale > MAX_SCALE)
            mHistory.scale = MAX_SCALE
        else
            mHistory.scale = mScale
    }

    /**
     * 绘制当前Model边框
     */
    protected fun drawFrame(canvas: Canvas, bounds: RectF) {
        mFramePath.reset()
        mFramePath.moveTo(bounds.left, bounds.top)
        mFramePath.quadTo(bounds.left, bounds.top, bounds.right, bounds.top)
        mFramePath.quadTo(bounds.right, bounds.top, bounds.right, bounds.bottom)
        mFramePath.quadTo(bounds.right, bounds.bottom, bounds.left, bounds.bottom)
        mFramePath.quadTo(bounds.left, bounds.bottom, bounds.left, bounds.top)
        canvas.drawPath(mFramePath, mFramePaint)
    }

    /**
     * @param isRotate 是否旋转
     * @param rotate 旋转角度
     * @param isMove 是否移动
     * @param x x轴移动距离
     * @param y y轴移动距离
     * @param isScale 是否缩放
     * @param scale 缩放比例
     */
    protected data class History(var centerX: Float = 0f,
                                 var centerY: Float = 0f,
                                 var isRotate: Boolean = false,
                                 var rotate: Float = 0f,
                                 var isMove: Boolean = false,
                                 var x: Float = 0f,
                                 var y: Float = 0f,
                                 var isScale: Boolean = false,
                                 var scale: Float = 1f)
}