package com.github.cccxm.palette.controller

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextWatcher

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
 *  写字板控制器接口
 */
interface WordPad {
    /**
     * 线段类型
     */
    enum class LineType(val drawable: Drawable?) {
        /**
         * 实线(Solid Line)
         */
        SOLID(object : GradientDrawable() {
            init {
                cornerRadius = 2f
                setStroke(1, Color.BLACK)
            }
        }),
        /**
         * 短划线 (Dashed Line)
         */
        DASHED(object : GradientDrawable() {
            init {
                cornerRadius = 2f
                setStroke(1, Color.BLACK, 3f, 2f)
            }
        }),
        /**
         * 虚线 (Dotted Line)
         */
        DOTTED(object : GradientDrawable() {
            init {
                cornerRadius = 2f
                setStroke(1, Color.BLACK, 1f, 1f)
            }
        }),
        /**
         * 无类型
         */
        NULL(null);
    }

    /**
     * 字体
     */
    enum class Font(val value: String) {
        黑体("黑体"),
        宋体("宋体");
    }

    /**
     * 字体类型
     */
    enum class FontType(val value: Int) {
        /**
         * 正常字体
         */
        NORMAL(Typeface.NORMAL),
        /**
         * 粗体
         */
        BOLD(Typeface.BOLD),
        /**
         * 加粗斜体
         */
        BOLD_ITALIC(Typeface.BOLD_ITALIC),
        /**
         * 斜体
         */
        ITALIC(Typeface.ITALIC);
    }

    /**
     * 设置写字板监听对象
     */
    fun addTextChangedListener(listener: TextWatcher)

    /**
     * 设置边框线类型
     */
    fun setLineType(type: LineType)

    /**
     * 设置字体。
     */
    fun setFont(font: Font, type: FontType)

    /**
     * 设置字体大小。
     */
    fun setFontSize(size: Float)

    /**
     * 设置字体颜色。
     */
    fun setFontColor(color: Int)
}