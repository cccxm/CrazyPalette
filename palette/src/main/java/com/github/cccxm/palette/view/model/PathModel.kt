package com.github.cccxm.palette.view.model

import android.graphics.*

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
 *
 *  该Model显示栈顶的Path
 */
class PathModel(val paint: Paint, val path: Path) : Model() {
    private val mPath = Path()
    private val mMatrix = Matrix()

    override fun drawHistory(canvas: Canvas, history: History, isBounds: Boolean): Boolean {
        mPath.reset()
        mMatrix.reset()
        mPath.addPath(path)
        if (history.isMove) {
            mPath.offset(history.x, history.y)
        }
        if (history.isScale) {
            mMatrix.setScale(history.scale, history.scale, history.centerX, history.centerY)
        }
        if (history.isRotate) {
            mMatrix.postRotate(history.rotate, history.centerX, history.centerY)
        }
        mPath.transform(mMatrix)
        canvas.drawPath(mPath, paint)
        if (isBounds) {
            mPath.computeBounds(frameBounds, true)
            frameBounds.addWidth(paint.strokeWidth)
        }
        return isBounds
    }
}