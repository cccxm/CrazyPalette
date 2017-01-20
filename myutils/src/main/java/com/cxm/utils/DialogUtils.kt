package com.cxm.utils

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

/**
 * 对话框工具类
 * @author cxm 16/9/1.
 *
 * @param title 对话框标题，可以为空
 * @param layout 布局id，为0时表示这是普通对话框
 * @param message 消息，没有布局时显示此消息
 */
class DialogUtils(val title: String? = null,
                  val layout: Int = 0,
                  val message: String = "",
                  val prepare: ((View) -> Unit) = {}) : DialogFragment() {

    private var positiveName: String? = null
    private var negativeName: String? = null
    private var positiveCallback: ((DialogFragment) -> Unit)? = null
    private var negativeCallback: ((DialogFragment) -> Unit)? = null

    fun setPositiveName(name: String, cb: (DialogFragment) -> Unit): DialogUtils {
        this.positiveName = name
        this.positiveCallback = cb
        return this
    }

    fun setNegativeName(name: String, cb: (DialogFragment) -> Unit): DialogUtils {
        this.negativeName = name
        this.negativeCallback = cb
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (layout != 0) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            val view = inflater.inflate(layout, container, false)
            prepare(view)
            return view
        } else
            return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return if (layout == 0) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setMessage(message)
            if (positiveName != null) {
                builder.setPositiveButton(positiveName, { dialog, which ->
                    positiveCallback?.invoke(this)
                })
            }
            if (negativeName != null) {
                builder.setNegativeButton(negativeName, { dialog, which ->
                    negativeCallback?.invoke(this)
                })
            }
            builder.create()
        } else super.onCreateDialog(savedInstanceState)
    }
}