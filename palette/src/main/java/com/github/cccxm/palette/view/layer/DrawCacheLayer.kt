package com.github.cccxm.palette.view.layer

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.view.MotionEvent
import com.github.cccxm.palette.util.CanvasUtil
import com.github.cccxm.palette.util.HistoryUtil

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
 *  Created by 陈小默 on 2017/1/17.
 */
class DrawCacheLayer(format: Bitmap.Config?,
                     width: Int, height: Int,
                     paint: Paint)
    : CacheLayer(format, width, height, paint) {

    init {
        isDrawing = true
    }

    /**
     * 多点触控历史存储
     */
    private val mMultiTouchHistory = SparseArray<CasualPath>()

    override fun onTouchEvent(event: MotionEvent) {
        clearCache()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val id = event.getPointerId(event.actionIndex)
                if (!isMultiTouch && id != 0) return
                val x = event.getX(id)
                val y = event.getY(id)
                val history = CasualPath(CanvasUtil.paintCopy(paint))
                mMultiTouchHistory.put(id, history)
                history.path.moveTo(x, y)
                history.preX = x
                history.preY = y
                mClearRedo()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val id = event.getPointerId(event.actionIndex)
                val history = mMultiTouchHistory[id]
                if (history != null) {
                    mMultiTouchHistory.remove(id)
                    mAddHistory(HistoryUtil.toHistory(history.paint, history.path))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                (0 until event.pointerCount)
                        .map { event.getPointerId(it) }
                        .filter { mMultiTouchHistory[it] != null }
                        .forEach {
                            val history = mMultiTouchHistory[it]
                            val preY = history.preY
                            val preX = history.preX
                            val x = event.getX(it)
                            val y = event.getY(it)
                            if (Math.abs(x - preX) > 3 || Math.abs(y - preY) > 3) {
                                history.path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2)
                                history.preX = x
                                history.preY = y
                            }
                        }
            }
        }
        invalidate()
    }

    private fun invalidate() {
        (0 until mMultiTouchHistory.size()).forEach {
            val his = mMultiTouchHistory.valueAt(it)
            cacheCanvas.drawPath(his.path, his.paint)
        }
    }

    private data class CasualPath(var paint: Paint,
                                  var path: Path = Path(),
                                  var preX: Float = 0f,
                                  var preY: Float = 0f)
}