package com.cxm.mvp

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.cxm.utils.ActivityUtils

/**
 *  project:mutils
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
 *  Created by 陈小默 on 2016/8/12.
 */
abstract class BaseActivity<P : IPresenter> : AppCompatActivity() {
    /**
     * 是否允许全屏
     */
    protected var allowFullScreen = false
    /**
     * 是否允许ActionBar
     */
    protected var allowActionBar = false
    /**
     * 选择方向
     */
    protected var orientation = Orientation.LANDSCAPE

    protected var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = orientation.value
        if (allowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (!allowActionBar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            supportActionBar?.hide()
        }
        ActivityUtils.registerActivity(this)
    }

    override fun finish() {
        presenter = null
        ActivityUtils.unregisterActivity(this)
        super.finish()
    }

    /**
     * 当前Activity的根布局
     */
    val root: View get() = (findViewById(android.R.id.content) as ViewGroup).getChildAt(0)

    /**
     * 当前屏幕的高度(像素)
     */
    val height: Int get() = resources.displayMetrics.heightPixels

    /**
     * 当前屏幕的宽度(像素)
     */
    val width: Int get() = resources.displayMetrics.widthPixels

    enum class Orientation(val value: Int) {
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
        AUTO(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    fun toast(message: Any?) {
        Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
    }
}