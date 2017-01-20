package com.github.cccxm.palette.view.layer

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
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
 *  Created by 陈小默 on 2017/1/11.
 */
class BitmapCacheLayer(private val mBitmap: BitmapContainer,
                       format: Bitmap.Config?,
                       width: Int, height: Int)
    : CacheLayer(format, width, height, Paint()) {

    private var startX = 0f
    private var startY = 0f
    private var bounds = RectF()
    override fun onTouchEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y
        clearCache()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                startX = x
                startY = y
                mClearRedo()
            }
            MotionEvent.ACTION_UP -> {
                isDrawing = false
                mAddHistory(HistoryUtil.toHistory(mBitmap.bitmap!!, RectF(bounds)))
            }
            MotionEvent.ACTION_MOVE -> {
                bounds.top = if (startY < y) startY else y
                bounds.bottom = if (startY >= y) startY else y
                bounds.left = if (startX < x) startX else x
                bounds.right = if (startX >= x) startX else x
                cacheCanvas.drawBitmap(mBitmap.bitmap, null, bounds, paint)
            }
        }
    }

    class BitmapContainer {
        var bitmap: Bitmap? = null
    }
}