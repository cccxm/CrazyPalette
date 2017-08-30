package com.github.cccxm.crazypalette

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.github.cccxm.palette.controller.Controller
import com.github.cccxm.palette.controller.WordPad
import com.github.cccxm.palette.factory.BoardFactory
import com.github.cccxm.palette.view.shape.CircleShape
import com.github.cccxm.palette.view.shape.LineShape
import com.github.cccxm.palette.view.shape.OvalShape
import com.github.cccxm.palette.view.shape.RectShape
import org.jetbrains.anko.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val GRAY1 = 0xD1.gray.opaque
        private val GRAY2 = 0x96.gray.opaque
        private val GRAY3 = 0x66.gray.opaque

        /**
         * 处理联动操作的类
         */
        private class Group {
            private val list = ArrayList<View>()

            fun addView(view: View, default: Boolean = false, listener: View.() -> Unit) {
                list.add(view)
                if (default) {
                    view.backgroundColor = GRAY2
                }
                view.setOnClickListener {
                    select(it)
                    it.listener()
                }
            }

            private fun select(view: View) {
                list.forEach { it.backgroundColor = GRAY1 }
                view.backgroundColor = GRAY2
            }
        }

        /**
         * 按钮被点击的联动操作
         */
        private fun View.click(group: Group, default: Boolean = false, listener: View.() -> Unit = {}) {
            group.addView(this, default, listener)
        }

        /**
         * 修改当前颜色的透明度，透明度取址范围 0-0xFF
         */
        private fun buildColor(color: Int, alpha: Int): Int {
            var mColor = color
            mColor = mColor and 0x00FFFFFF
            val mAlpha = alpha shl 24
            return mColor or mAlpha
        }
    }

    private lateinit var controller: Controller

    private lateinit var mPaintWindow: PopupWindow
    private lateinit var mEraserWindow: PopupWindow
    private lateinit var mTextWindow: PopupWindow
    private lateinit var mTextPad: WordPad

    private val mPaintTypeGroup = Group()
    private val mPaintShapeGroup = Group()
    private val mPaintColorGroup = Group()
    private val mTextFontGroup = Group()
    private val mTextFontTypeGroup = Group()
    private val mTextFontColorGroup = Group()
    private val mTextFrameGroup = Group()

    private var mCurrentPaintColor: Int = Color.BLACK
    private var mCurrentPaintAlpha: Int = 0xff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        val factory = BoardFactory.getFactory()
        controller = factory.getController(this)
        val view = controller.getView()
        controller.setBackgroundColor(Color.WHITE)
        createPaintWindow()
        createTextWindow()
        createEraserWindow()
        linearLayout {
            lparams {
                width = matchParent
                height = matchParent
            }
            backgroundColor = GRAY3
            orientation = LinearLayout.HORIZONTAL
            linearLayout {
                //画图板
                lparams {
                    width = 0
                    height = matchParent
                    weight = 1f
                }
                addView(view)
                padding = dip(50)
            }
            scrollView {
                lparams {
                    width = dip(120)
                    height = matchParent
                }
                linearLayout {
                    //控制器面板
                    lparams(matchParent, wrapContent)
                    backgroundColor = GRAY2
                    orientation = LinearLayout.VERTICAL
                    switch(R.style.ButtonStyle) {
                        //多点触控开关
                        isChecked = true
                        onCheckedChange { _, switch ->
                            controller.multiTouch = switch
                            toast(if (switch)
                                "已经开启多点触控模式"
                            else
                                "多点触摸模式关闭，需要多指交互的操作都会失效")
                        }
                    }
                    textView(text = "设置画笔", theme = R.style.ButtonStyle) {
                        onClick {
                            if (!mPaintWindow.isShowing)
                                mPaintWindow.showAtLocation(root, Gravity.END or Gravity.TOP,
                                        dip(120), dip(50))
                        }
                    }
                    textView(text = "写字设置", theme = R.style.ButtonStyle) {
                        onClick {
                            mTextPad = controller.drawText()
                            controller.setCommand(Controller.Command.TEXT)
                            if (!mTextWindow.isShowing)
                                mTextWindow.showAtLocation(root, Gravity.END or Gravity.TOP,
                                        dip(120), dip(100))
                        }
                    }
                    textView(text = "橡皮设置", theme = R.style.ButtonStyle) {
                        onClick {
                            controller.setCommand(Controller.Command.ERASER)
                            if (!mEraserWindow.isShowing)
                                mEraserWindow.showAtLocation(root, Gravity.END or Gravity.TOP,
                                        dip(120), dip(150))
                        }
                    }
                    textView(text = "撤销", theme = R.style.ButtonStyle) {
                        onClick {
                            controller.undo()
                        }
                    }
                    textView(text = "重做", theme = R.style.ButtonStyle) {
                        onClick {
                            controller.redo()
                        }
                    }
                    textView(text = "添加图片", theme = R.style.ButtonStyle) {
                        //TODO
                    }
                    textView(text = "设置背景", theme = R.style.ButtonStyle) {
                        //TODO
                    }
                    switch(R.style.ButtonStyle) {
                        isChecked = false
                        onCheckedChange { _, switch ->
                            if (switch) {
                                toast("禁用绘图")
                                controller.setCommand(Controller.Command.DISABLE)
                            } else {
                                toast("启用绘图")
                            }
                        }
                    }
                    textView(text = "保存", theme = R.style.ButtonStyle) {
                        //TODO
                    }
                    textView(text = "清屏", theme = R.style.ButtonStyle) {
                        onClick {
                            alert(title = "提醒", message = "是否清空画板内容") {
                                yesButton {
                                    controller.clear()
                                }
                                noButton {}
                            }.show()
                        }
                    }
                    textView(text = "爆炸", theme = R.style.ButtonStyle) {
                        onClick { controller.setCommand(Controller.Command.EXPLODE) }
                    }
                }
            }
        }
    }

    /**
     * 初始化橡皮设置弹出窗口
     */
    private fun createEraserWindow() {
        val settingsLayout = with(this) {
            linearLayout {
                lparams {
                    width = wrapContent
                    height = wrapContent
                    padding = dip(10)
                }
                backgroundColor = GRAY1
                seekBar {
                    lparams {
                        width = dip(200)
                        height = wrapContent
                    }
                    max = 50
                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            controller.setStrokeWidth(it!!.progress + 5f)
                        }
                    }
                }
            }
        }
        mEraserWindow = createWindow(settingsLayout)
    }

    /**
     * 当前Activity的根布局
     */
    val root: View get() = (findViewById(android.R.id.content) as ViewGroup).getChildAt(0)

    /**
     * 初始化写字板设置窗口
     */
    private fun createTextWindow() {
        val settingsLayout = with(this) {
            linearLayout {
                lparams {
                    width = wrapContent
                    height = wrapContent
                    padding = dip(10)
                }
                backgroundColor = GRAY1
                orientation = LinearLayout.VERTICAL

                textView(text = "字体", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "黑体", theme = R.style.TextStyle) {
                        click(mTextFontGroup, true)
                    }
                    textView(text = "宋体", theme = R.style.TextStyle) {
                        click(mTextFontGroup)
                    }
                }
                textView(text = "边框类型", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "直线", theme = R.style.TextStyle) {
                        click(mTextFrameGroup) {
                            mTextPad.setLineType(WordPad.LineType.SOLID)
                        }
                    }
                    textView(text = "短划线", theme = R.style.TextStyle) {
                        click(mTextFrameGroup, true) {
                            mTextPad.setLineType(WordPad.LineType.DASHED)
                        }
                    }
                    textView(text = "虚线", theme = R.style.TextStyle) {
                        click(mTextFrameGroup) {
                            mTextPad.setLineType(WordPad.LineType.DOTTED)
                        }
                    }
                    textView(text = "无边框", theme = R.style.TextStyle) {
                        click(mTextFrameGroup) {
                            mTextPad.setLineType(WordPad.LineType.NULL)
                        }
                    }
                }
                textView(text = "字体类型", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "正常", theme = R.style.TextStyle) {
                        click(mTextFontTypeGroup, true) {
                            mTextPad.setFont(WordPad.Font.黑体, WordPad.FontType.NORMAL)
                        }
                    }
                    textView(text = "粗体", theme = R.style.TextStyle) {
                        click(mTextFontTypeGroup) {
                            mTextPad.setFont(WordPad.Font.黑体, WordPad.FontType.BOLD)
                        }
                    }
                    textView(text = "斜体", theme = R.style.TextStyle) {
                        click(mTextFontTypeGroup) {
                            mTextPad.setFont(WordPad.Font.黑体, WordPad.FontType.ITALIC)
                        }
                    }
                    textView(text = "加粗斜体", theme = R.style.TextStyle) {
                        click(mTextFontTypeGroup) {
                            mTextPad.setFont(WordPad.Font.黑体, WordPad.FontType.BOLD_ITALIC)
                        }
                    }
                }
                textView(text = "字号", theme = R.style.TextStyle)
                seekBar {
                    lparams {
                        width = matchParent
                        height = wrapContent
                    }
                    max = 70
                    progress = 12
                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            mTextPad.setFontSize(it!!.progress + 12f)
                        }
                    }
                }
                textView(text = "字体颜色", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "黑色", theme = R.style.TextStyle) {
                        click(mTextFontColorGroup, true) {
                            mTextPad.setFontColor(Color.BLACK)
                        }
                    }
                    textView(text = "红色", theme = R.style.TextStyle) {
                        click(mTextFontColorGroup) {
                            mTextPad.setFontColor(Color.RED)
                        }
                    }
                    textView(text = "蓝色", theme = R.style.TextStyle) {
                        click(mTextFontColorGroup) {
                            mTextPad.setFontColor(Color.BLUE)
                        }
                    }
                    textView(text = "绿色", theme = R.style.TextStyle) {
                        click(mTextFontColorGroup) {
                            mTextPad.setFontColor(Color.GREEN)
                        }
                    }
                    textView(text = "黄色", theme = R.style.TextStyle) {
                        click(mTextFontColorGroup) {
                            mTextPad.setFontColor(Color.YELLOW)
                        }
                    }
                }
            }
        }
        mTextWindow = createWindow(settingsLayout)
    }

    /**
     * 创建画笔设置窗口
     */
    private fun createPaintWindow() {
        val settingsLayout = with(this) {
            linearLayout {
                lparams {
                    width = wrapContent
                    height = wrapContent
                    padding = dip(10)
                }
                backgroundColor = GRAY1
                orientation = LinearLayout.VERTICAL
                textView(text = "画笔类型", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "铅笔", theme = R.style.TextStyle) {
                        click(mPaintTypeGroup, true)
                    }
                    textView(text = "蜡笔", theme = R.style.TextStyle) {
                        click(mPaintTypeGroup)
                    }
                    textView(text = "刷子", theme = R.style.TextStyle) {
                        click(mPaintTypeGroup)
                    }
                }
                textView(text = "图形选择", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "无", theme = R.style.TextStyle) {
                        click(mPaintShapeGroup, true) {
                            controller.setCommand(Controller.Command.DRAW)
                        }
                    }
                    textView(text = "直线", theme = R.style.TextStyle) {
                        click(mPaintShapeGroup) {
                            controller.drawShape(LineShape())
                            controller.setCommand(Controller.Command.SHAPE)
                        }
                    }
                    textView(text = "矩形", theme = R.style.TextStyle) {
                        click(mPaintShapeGroup) {
                            controller.drawShape(RectShape())
                            controller.setCommand(Controller.Command.SHAPE)
                        }
                    }
                    textView(text = "椭圆", theme = R.style.TextStyle) {
                        click(mPaintShapeGroup) {
                            controller.drawShape(OvalShape())
                            controller.setCommand(Controller.Command.SHAPE)
                        }
                    }
                    textView(text = "圆", theme = R.style.TextStyle) {
                        click(mPaintShapeGroup) {
                            controller.drawShape(CircleShape(true))
                            controller.setCommand(Controller.Command.SHAPE)
                        }
                    }
                }
                textView(text = "画笔尺寸", theme = R.style.TextStyle)
                seekBar {
                    lparams {
                        width = matchParent
                        height = wrapContent
                    }
                    max = 30
                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            controller.setStrokeWidth(it!!.progress + 1f)
                        }
                    }
                }
                textView(text = "画笔颜色", theme = R.style.TextStyle)
                linearLayout(R.style.HorizontalStyle) {
                    textView(text = "黑色", theme = R.style.TextStyle) {
                        click(mPaintColorGroup, true) {
                            mCurrentPaintColor = Color.BLACK
                            val color = buildColor(mCurrentPaintColor, mCurrentPaintAlpha)
                            controller.setStrokeColor(color)
                        }
                    }
                    textView(text = "红色", theme = R.style.TextStyle) {
                        click(mPaintColorGroup) {
                            mCurrentPaintColor = Color.RED
                            val color = buildColor(mCurrentPaintColor, mCurrentPaintAlpha)
                            controller.setStrokeColor(color)
                        }
                    }
                    textView(text = "蓝色", theme = R.style.TextStyle) {
                        click(mPaintColorGroup) {
                            mCurrentPaintColor = Color.BLUE
                            val color = buildColor(mCurrentPaintColor, mCurrentPaintAlpha)
                            controller.setStrokeColor(color)
                        }
                    }
                    textView(text = "黄色", theme = R.style.TextStyle) {
                        click(mPaintColorGroup) {
                            mCurrentPaintColor = Color.YELLOW
                            val color = buildColor(mCurrentPaintColor, mCurrentPaintAlpha)
                            controller.setStrokeColor(color)
                        }
                    }
                    textView(text = "绿色", theme = R.style.TextStyle) {
                        click(mPaintColorGroup) {
                            mCurrentPaintColor = Color.GREEN
                            val color = buildColor(mCurrentPaintColor, mCurrentPaintAlpha)
                            controller.setStrokeColor(color)
                        }
                    }
                }
                textView(text = "不透明度", theme = R.style.TextStyle)
                seekBar {
                    lparams {
                        width = matchParent
                        height = wrapContent
                    }
                    max = 255
                    progress = 255
                    onSeekBarChangeListener {
                        onStopTrackingTouch {
                            mCurrentPaintAlpha = it!!.progress
                            mCurrentPaintColor = buildColor(mCurrentPaintColor, mCurrentPaintColor)
                            controller.setStrokeColor(mCurrentPaintColor)
                        }
                    }
                }
            }
        }
        mPaintWindow = createWindow(settingsLayout)
    }

    private fun createWindow(view: View): PopupWindow {
        return with(PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)) {
            isTouchable = true
            isFocusable = true
            isOutsideTouchable = true
            val bac: Bitmap? = null
            setBackgroundDrawable(BitmapDrawable(resources, bac))
            this
        }
    }
}
