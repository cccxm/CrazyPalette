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
 *  Created by 陈小默 on 2017/1/4.
 */
abstract class Shape {
    /**
     * 是否填充内部区域
     */
    open var isFull = false

    /**
     * 返回想要显示的图形Path
     *
     * @param x 图像起始位置的X坐标
     * @param y 图像起始位置的Y坐标
     * @param endX 图像结束位置的X坐标
     * @param endY 图像结束为止的Y坐标
     * @return 需要显示的Path
     */
    abstract fun getShape(x: Float, y: Float, endX: Float, endY: Float): Path
}