package com.github.cccxm.palette.util

import android.util.Log

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
 *  Created by 陈小默 on 2017/1/3.
 *
 *  日志输出类
 */
class Logger(private val tag: String) {
    constructor(clazz: Class<*>) : this(clazz.name)
    constructor(any: Any) : this(any.javaClass)

    fun e(any: Any?) {
        Log.e(tag, "$any")
    }

    fun d(any: Any?) {
        Log.d(tag, "$any")
    }

    fun i(any: Any?) {
        Log.i(tag, "$any")
    }

    fun v(any: Any?) {
        Log.v(tag, "$any")
    }

    fun w(any: Any?) {
        Log.w(tag, "$any")
    }
}