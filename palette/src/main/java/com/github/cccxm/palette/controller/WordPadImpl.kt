package com.github.cccxm.palette.controller

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextWatcher
import android.util.TypedValue
import com.github.cccxm.palette.view.layer.TextCacheLayer

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
 *  Created by 陈小默 on 2017/1/9.
 */
class WordPadImpl : WordPad {

    /**
     * 写字板缓冲层对象
     */
    private var mLayer: TextCacheLayer? = null

    fun setLayer(layer: TextCacheLayer) {
        mLayer = layer
        initConfig()
    }

    /**
     * 初始化配置信息
     */
    private fun initConfig() {
        val paint = mLayer?.paint ?: return
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        setFontColor(Color.BLACK)
        setFontSize(22f)
        setFont(WordPad.Font.宋体, WordPad.FontType.NORMAL)
        setLineType(WordPad.LineType.SOLID)
    }

    override fun addTextChangedListener(listener: TextWatcher) {
        mLayer?.listener = listener
    }

    override fun setLineType(type: WordPad.LineType) {
        mLayer?.mEditView?.background = type.drawable
    }

    override fun setFont(font: WordPad.Font, type: WordPad.FontType) {
        mLayer?.mEditView?.typeface = Typeface.create(font.value, type.value)
        mLayer?.paint?.typeface = Typeface.create(font.value, type.value)
    }

    override fun setFontSize(size: Float) {
        mLayer?.mEditView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        mLayer?.paint?.textSize = size
    }

    override fun setFontColor(color: Int) {
        mLayer?.mEditView?.setTextColor(color)
        mLayer?.paint?.color = color
    }
}