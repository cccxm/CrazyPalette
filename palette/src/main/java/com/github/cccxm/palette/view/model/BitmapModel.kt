package com.github.cccxm.palette.view.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
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
 *  Created by 陈小默 on 2017/1/16.
 */
class BitmapModel(private val bitmap: Bitmap,
                  private val bounds: RectF) : Model() {
    private val mBounds = RectF()
    private val mMatrix = Matrix()
    override fun drawHistory(canvas: Canvas, history: History, isBounds: Boolean): Boolean {
        mBounds.set(bounds)
        mMatrix.reset()
        if (history.isMove) {
            mMatrix.setTranslate(history.x, history.y)
            mBounds.move(history.x, history.y)
        }
        canvas.save()
        canvas.matrix = mMatrix
        canvas.drawBitmap(bitmap, null, bounds, null)
        canvas.restore()
        if (isBounds) {
            frameBounds = RectF(mBounds)
            frameBounds.addWidth(5f)
        }
        return isBounds
    }
}