package com.github.cccxm.palette.factory

import android.content.Context
import com.github.cccxm.palette.controller.Controller

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
 */
abstract class BoardFactory {
    companion object {
        fun getFactory(): BoardFactory = BoardFactoryImpl()
    }

    /**
     * 获取绘图板控制器。该控制器是单例存在的，当画板显示时可用，当画板隐藏后调用无效
     *
     * @return 绘图板控制器
     */
    abstract fun getController(context: Context): Controller
}