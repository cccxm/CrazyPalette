package com.github.cccxm.palette.view.layer

import android.graphics.Bitmap
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.PopupWindow
import com.github.cccxm.palette.util.CanvasUtil
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
 *  Created by 陈小默 on 2017/1/9.
 */
class TextCacheLayer(val view: View,
                     format: Bitmap.Config?,
                     width: Int, height: Int)
    : CacheLayer(format, width, height, Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)), TextWatcher {

    /**
     * 使用者设置的EditText文件改变的事件监听对象
     */
    var listener: TextWatcher? = null

    /**
     * 字体的绘制位置
     */

    private var startX = 0f
    private var startY = 0f

    /**
     * Window的显示位置
     */
    private var rawX = 0f
    private var rawY = 0f

    /**
     * 用来保存文字输入框尺寸的对象
     */
    private var preBounds = Bounds(0, 0)

    /**
     * 某些Android版本上无法正常正常使用PopupWindow的该状态
     */
    private var isShowing = false

    val mEditView = EditText(view.context).apply {
        addTextChangedListener(this@TextCacheLayer)
        gravity = Gravity.TOP
    }

    val mWindow = PopupWindow(mEditView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true).apply {
        isTouchable = true
        isFocusable = true
        isOutsideTouchable = true
        setTouchModel(false)
    }

    /**
     * 当此层被推出时调用，用来隐藏已经显示的窗口
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isShowing){
            mWindow.dismiss()
            isShowing = false
        }
    }

    /**
     * 只监听按下事件，如果Window正在显示则隐藏，否则保存坐标点然后显示窗口
     */
    override fun onTouchEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (isShowing) {
                dismissTextWindow()
            } else {
                startX = event.x
                startY = event.y
                rawX = event.rawX
                rawY = event.rawY
                showTextWindow()
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        listener?.afterTextChanged(s)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        listener?.beforeTextChanged(s, start, count, after)
    }

    /**
     * 在文字发生改变的时候测量应该显示的窗口尺寸，然后重新显示窗口
     */
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        listener?.onTextChanged(s, start, before, count)
        val bounds = getBounds(startX, startY, s.toString())
        if (bounds.isChange(preBounds)) {
            preBounds = bounds
            val (width, height) = preBounds
            mEditView.width = width
            mEditView.height = height
            mWindow.showAtLocation(view, Gravity.NO_GRAVITY, rawX.toInt(), rawY.toInt())
            isShowing = true
        }
    }

    /**
     * 用于显示EditText，在显示之前应该先测量边界，以保证Window不会超出当前SurfaceView。
     */
    private fun showTextWindow() {
        preBounds = getBounds(startX, startY)
        val (width, height) = preBounds
        mEditView.width = width
        mEditView.height = height
        mWindow.showAtLocation(view, Gravity.NO_GRAVITY, rawX.toInt(), rawY.toInt())
        isShowing = true
    }

    /**
     * 并且在每一次显示之前应该清空EditText
     */
    private fun dismissTextWindow() {
        drawText(CanvasUtil.paintCopy(paint), mEditView.text.toString(), startX, startY)
        mEditView.setText("")
        mWindow.dismiss()
        isShowing = false
    }

    /**
     * 将当前文字绘制在屏幕上并加入历史记录
     */
    private fun drawText(paint: Paint, text: String, x: Float, y: Float) {
        if (text.isNotBlank()) {
            mAddHistory(HistoryUtil.toHistory(paint, text, x, y + mEditView.lineHeight + 10))
        }
    }

    private data class Bounds(val width: Int, val height: Int) {
        fun isChange(bounds: Bounds): Boolean = this != bounds
    }

    /**
     * 获取应该显示的窗口尺寸，对于没有字显示的时候显示默认尺寸，否则测量行数与列宽。
     * 在所有测量完毕之后根据SurfaceView的尺寸进行裁剪使得窗口大小不会超出屏幕
     */
    private fun getBounds(x: Float, y: Float, text: String? = null): Bounds {
        var lineCount = 1
        var width = 150
        if (text != null) {
            val arr = text.split("[\r\n]".toRegex())
            lineCount = arr.size
            arr.forEach {
                val measure = paint.measureText(it) + 10
                if (measure + x > this.width)
                    lineCount++
                if (measure > width)
                    width = measure.toInt()
            }
        }
        var height = lineCount * mEditView.lineHeight
        height += 35
        if (width + x > this.width)
            width = (this.width - x).toInt()
        if (height + y > this.height)
            height = (this.height - y).toInt()
        return Bounds(width, height)
    }

    /**
     * 设置PopupWindow的外侧点击事件向下传递
     */
    private fun PopupWindow.setTouchModel(touchMode: Boolean) {
        try {
            val method = this.javaClass.getDeclaredMethod("setTouchModal", Boolean::class.java)
            method.isAccessible = true
            method.invoke(this, touchMode)
        } catch (e: Exception) {
        }
    }
}