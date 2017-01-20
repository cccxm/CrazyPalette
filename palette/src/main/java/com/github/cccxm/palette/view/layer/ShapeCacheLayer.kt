package com.github.cccxm.palette.view.layer

import android.graphics.Bitmap
import android.graphics.Paint
import android.view.MotionEvent
import com.github.cccxm.palette.util.CanvasUtil
import com.github.cccxm.palette.util.HistoryUtil
import com.github.cccxm.palette.view.shape.Shape


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
 */
class ShapeCacheLayer(val shape: Shape,
                      format: Bitmap.Config?,
                      width: Int, height: Int,
                      paint: Paint)
    : CacheLayer(format, width, height, paint) {
    private lateinit var mPaint: Paint
    private var preX = 0f
    private var preY = 0f
    override fun onTouchEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mPaint = CanvasUtil.paintCopy(paint)
                if (shape.isFull)
                    mPaint.style = Paint.Style.FILL_AND_STROKE
                preX = x
                preY = y
                mClearRedo()
            }
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                val path = CanvasUtil.pathCopy(shape.getShape(preX, preY, x, y))
                mAddHistory(HistoryUtil.toHistory(mPaint, path))
                clearCache()
            }
            MotionEvent.ACTION_MOVE -> {
                val path = shape.getShape(preX, preY, x, y)
                clearCache()
                cacheCanvas.drawPath(path, mPaint)
            }
        }
    }
}