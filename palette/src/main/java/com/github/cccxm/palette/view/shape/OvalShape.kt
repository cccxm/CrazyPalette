package com.github.cccxm.palette.view.shape

import android.graphics.Path
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
 *  Created by 陈小默 on 2017/1/6.
 *
 *  椭圆模型
 *
 *  @param isOrigin 手指点下的点是否是原点，如果不设置或设置为false表示手指点下的点为图形所在矩形上的一个角
 */
open class OvalShape(val isOrigin: Boolean = false,
                     override var isFull: Boolean = false) : Shape() {

    private val path = Path()
    private val rectF = RectF()
    override fun getShape(x: Float, y: Float, endX: Float, endY: Float): Path {
        path.reset()
        if (isOrigin) {
            val nx = x + x - endX
            val ny = y + y - endY
            val left = if (nx <= endX) nx else endX
            val right = if (nx <= endX) endX else nx
            val top = if (ny <= endY) ny else endY
            val bottom = if (ny <= endY) endY else ny
            rectF.set(left, top, right, bottom)
        } else {
            val left = if (x <= endX) x else endX
            val right = if (x <= endX) endX else x
            val top = if (y <= endY) y else endY
            val bottom = if (y <= endY) endY else y
            rectF.set(left, top, right, bottom)
        }
        path.addOval(rectF, Path.Direction.CCW)
        return path
    }
}