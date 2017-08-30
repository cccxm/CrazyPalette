package com.github.cccxm.palette.factory

import android.content.Context
import com.github.cccxm.palette.controller.Controller
import com.github.cccxm.palette.controller.ControllerImpl

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
class BoardFactoryImpl : BoardFactory() {
    override fun getController(context: Context): Controller = ControllerImpl(context)
}