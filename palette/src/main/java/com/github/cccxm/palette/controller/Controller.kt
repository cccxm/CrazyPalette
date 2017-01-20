package com.github.cccxm.palette.controller

import android.graphics.Bitmap
import android.view.SurfaceView
import com.github.cccxm.palette.view.shape.Shape

/**
 *  project:WhiteBoard
 *  Copyright (C) <2016>  <陈小默>
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
 *  Created by 陈小默 on 2016/12/29.
 *
 *  画板控制器接口
 */
interface Controller {
    enum class Command {
        /**
         * 禁用触摸事件，设置此标志位时所有触摸事件无效，用户不能在屏幕上绘制任何图形，但遥控器仍然可以控制面板。
         */
        DISABLE,
        /**
         * 开启触摸。此时用户可以正常绘制。事件直接降落在底层缓冲区。
         */
        DRAW,
        /**
         * 开启后触摸事件变为图形绘制事件。图形缓冲层创建并开始拦截事件。
         * 在切换到此命令之前，应该确保已经调用过 drawShape 方法设置过 Shape 对象。如果没有设置过对象则有可能会造成空指针异常。
         * 该命令将会检查绘图层是否存在，如果不存在则创建该层并插入图形对象。
         */
        SHAPE,
        /**
         * 开启中触摸事件变为字体绘制事件。文字缓冲层创建并开始拦截事件。
         * 在切换到此命令之前，应该确保已经调用过 drawText 方法获取写字板的遥控器对象。
         * 该命令将会检查文字绘图层是否存在，如果不存在则会创建该层。
         */
        TEXT,
        /**
         * 开启后触摸事件变为图片绘制事件。图片缓冲层创建并开始拦截事件。
         * 在切换到此命令之前，应该确保已经调用过 drawBitmap 方法设置图片。
         * 该命令将会检查图片绘图层是否存在，如果不存在则会创建该层。
         */
        BITMAP,
        /**
         * 开启后触摸绘图变为擦除。
         */
        ERASER,
        /**
         * 爆炸模式。用于将当前屏幕上的元素分散为独立的模块，使得用户可以单独操作某一个模块
         */
        EXPLODE;
    }

    /**
     * 是否开启多点触控，默认开启
     */
    var multiTouch: Boolean

    /**
     * 设置画笔粗细
     */
    fun setStrokeWidth(width: Float)

    /**
     * 设置画笔颜色
     */
    fun setStrokeColor(color: Int)

    /**
     * 设置画板背景颜色。
     */
    fun setBackgroundColor(color: Int)

    /**
     * 设置图形对象到当前图形缓冲区。
     * 注意：在设置图形后需要使用 setCommand 更改绘图命令为 SHAPE,该命令将会检查绘图层是否存在，如果不存在则创建该层并插入图形对象。
     */
    fun drawShape(shape: Shape)

    /**
     * 获取一个文字绘制遥控器。
     * 注意：在获取到遥控器后需要使用 setCommand 更改绘图命令为 TEXT，该命令将会检查文字绘图层是否存在，如果不存在则会创建该层。
     * 并且不能在获取到文字绘制遥控器之前切换命令到 TEXT ，应为绘图层需要遥控器来指挥绘制过程。
     * 该方法不保存状态。当切换到其他命令后该遥控器就会失效，所以在下次绘制文字之前仍然需要调用此方法获取新缓冲层的遥控器。
     */
    fun drawText(): WordPad

    /**
     * 设置需要绘制的图片。
     * 在图片绘图层启动的过程中可以多次设置图片对象。
     */
    fun drawBitmap(bitmap: Bitmap)

    /**
     * 撤销当前操作，该方法是容错的。如果没有更多的可撤销操作时不会抛出异常。
     */
    fun undo()

    /**
     * 当前是否能够继续撤销
     */
    fun hasUndo(): Boolean

    /**
     * 恢复当前操作，该方法是容错的。
     */
    fun redo()

    /**
     * 当前是否能够重做。
     */
    fun hasRedo(): Boolean

    /**
     * @return 当前白板的宽度(像素)
     */
    fun width(): Int

    /**
     * @return 当前白板的高度(像素)
     */
    fun height(): Int

    /**
     * 设置当前白板的可操作性
     */
    fun setCommand(command: Command)

    /**
     * 清空屏幕上的内容
     */
    fun clear()

    /**
     * 输出当前屏幕上的内容
     *
     * @return 包含当前屏幕内容的Bitmap
     */
    fun toPicture(): Bitmap

    /**
     * 获取画图板View对象
     *
     * @return 用于画图的视图对象
     */
    fun getView(): SurfaceView
}