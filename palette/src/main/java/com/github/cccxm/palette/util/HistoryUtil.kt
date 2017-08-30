@file:Suppress("MemberVisibilityCanPrivate")

package com.github.cccxm.palette.util

import android.graphics.*
import com.github.cccxm.palette.view.model.BitmapModel
import com.github.cccxm.palette.view.model.Model
import com.github.cccxm.palette.view.model.PathModel
import com.github.cccxm.palette.view.model.TextModel
import java.util.*

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
 *  Created by 陈小默 on 2017/1/10.
 */
class HistoryUtil(private val canvas: Canvas) {
    /**
     * 用来保存历史记录的栈。
     * 用以实现撤销和回复。
     */

    val mUndoStack = LinkedList<Model>()
    val mRedoStack = LinkedList<Model>()

    /**
     * 将历史添加进历史记录栈中，并立即绘制
     */
    fun addHistory(history: Model) {
        mUndoStack.push(history)
        history.draw(canvas) //TODO
    }

    /**
     * 撤销当前一步操作，如果该Model内可撤销，则撤销Model内操作
     */
    fun undo() {
        if (mUndoStack.isNotEmpty()) {
            CanvasUtil.clear(canvas)
            var his = mUndoStack.peek()
            if (his.hasUndo()) {
                his.undo()
            } else {
                his = mUndoStack.pop()
                mRedoStack.push(his)
            }
            mUndoStack.forEach { it.draw(canvas) }
        }
    }

    /**
     * 重做一步操作，如果该Model可重做，则重做该Model内操作
     */
    fun redo() {
        if (mRedoStack.isNotEmpty()) {
            if (mUndoStack.isEmpty()) {
                val his = mRedoStack.pop()
                his.draw(canvas)
                mUndoStack.push(his)
            } else {
                val peek = mUndoStack.peek()
                if (peek.hasRedo()) {
                    peek.redo()
                    CanvasUtil.clear(canvas)
                    mUndoStack.forEach { it.draw(canvas) }
                } else {
                    val his = mRedoStack.pop()
                    his.draw(canvas)
                    mUndoStack.push(his)
                }
            }
        }
    }

    /**
     * 清空重做栈
     */
    fun clearRedoStack() {
        mRedoStack.clear()
    }

    /**
     * 清空撤销栈、重做栈，并清空底层画板
     */
    fun clear() {
        mUndoStack.clear()
        mRedoStack.clear()
        CanvasUtil.clear(canvas)
    }

    /**
     * 当前是否有多余的操作可撤销
     */
    fun hasUndo(): Boolean = mUndoStack.isNotEmpty()

    /**
     * 当前是否有多余的操作可重做
     */
    fun hasRedo(): Boolean {
        return if (mRedoStack.isNotEmpty()) {
            true
        } else {
            if (mUndoStack.isNotEmpty()) {
                mUndoStack.peek().hasRedo()
            } else {
                false
            }
        }
    }

    /**
     * 清空底层缓冲区画板
     */
    fun clearCanvas() {
        CanvasUtil.clear(canvas)
    }

    /**
     * 清空底层缓冲区画板，并重新绘制所有图形
     */
    fun redrawCanvas() {
        clearCanvas()
        mUndoStack.forEach { it.draw(canvas) }
    }

    companion object {
        fun toHistory(paint: Paint, path: Path): Model = PathModel(paint, path)

        fun toHistory(paint: Paint, text: String, x: Float, y: Float): Model =
                TextModel(paint, text, x, y)

        fun toHistory(bitmap: Bitmap, bounds: RectF): Model = BitmapModel(bitmap, bounds)
    }
}