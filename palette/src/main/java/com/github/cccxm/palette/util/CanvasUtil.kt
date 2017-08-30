package com.github.cccxm.palette.util

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
 *  Created by 陈小默 on 2017/1/6.
 */
object CanvasUtil {
    /**
     * 用于清空画板的画笔
     */
    private val CLEAR_PAINT = Paint().apply {
        this.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    /**
     * 清空画布上的所有内容
     */
    fun clear(canvas: Canvas) {
        canvas.drawPaint(CLEAR_PAINT)
    }

    /**
     * 复制一个画笔对象，其中的属性完全复制于paint对象
     */
    fun paintCopy(paint: Paint): Paint {
        val mPaint = Paint()
        mPaint.set(paint)
        return mPaint
    }

    /**
     * 复制一个路径对象
     */
    fun pathCopy(path: Path): Path {
        val mPath = Path()
        mPath.set(path)
        return mPath
    }
}