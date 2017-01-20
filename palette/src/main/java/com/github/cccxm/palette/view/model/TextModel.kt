package com.github.cccxm.palette.view.model

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

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
class TextModel(val paint: Paint, val text: String, val x: Float, val y: Float) : Model() {
    override fun drawHistory(canvas: Canvas, history: History, isBounds: Boolean): Boolean {
        val bound = Rect()
        paint.getTextBounds(text, 0, text.length, bound)
        val height = bound.height()

        var x = this.x
        var y = this.y
        if (history.isMove) {
            x += history.x
            y += history.y
        }
        if (history.isRotate) {
            canvas.rotate(history.rotate, history.centerX, history.centerY)
        }
        text.split("[\n\r]".toRegex()).forEachIndexed { index, str ->
            canvas.drawText(str, x, y + (height * index), paint)
        }
        if (history.isRotate) {
            canvas.rotate(-history.rotate, history.centerX, history.centerY)
        }
        if (isBounds) {
            frameBounds = getBounds(x, y, text)
            frameBounds.addWidth(2f)
        }
        return isBounds
    }

    private fun getBounds(x: Float, y: Float, text: String): RectF {
        val arr = text.split("[\r\n]".toRegex())
        val lineCount = arr.size
        var width = 0
        arr.forEach {
            val measure = paint.measureText(it)
            if (measure > width)
                width = measure.toInt()
        }
        val singleHeight = paint.fontMetrics.bottom - paint.fontMetrics.top
        val height = lineCount * singleHeight
        val offset = singleHeight - paint.fontMetrics.descent
        return RectF(x, y - offset, x + width, y + height - offset)
    }
}