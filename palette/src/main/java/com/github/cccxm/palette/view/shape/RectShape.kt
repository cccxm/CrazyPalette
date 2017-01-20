package com.github.cccxm.palette.view.shape

import android.graphics.Path

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
 *
 *  矩形图像绘制对象
 */
class RectShape(override var isFull: Boolean = false) : Shape() {

    private val path = Path()
    override fun getShape(x: Float, y: Float, endX: Float, endY: Float): Path {
        path.reset()
        path.moveTo(x, y)
        path.quadTo(x, y, endX, y)
        path.quadTo(endX, y, endX, endY)
        path.quadTo(endX, endY, x, endY)
        path.quadTo(x, endY, x, y)
        return path
    }
}