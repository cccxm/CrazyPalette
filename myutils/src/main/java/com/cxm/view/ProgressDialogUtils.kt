package com.cxm.view

import android.app.ProgressDialog
import android.content.Context

/**
 * 进度对话框工具类
 * 陈小默 16/8/29.
 */
class ProgressDialogUtils(val context: Context) {
    private var progressDialog: ProgressDialog? = null

    /**
     * 初始化
     */
    private fun initDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.setCancelable(false)
    }

    fun show(message: String) {
        if (progressDialog == null)
            initDialog()
        progressDialog!!.setMessage(message)
        progressDialog!!.show()

    }

    fun dismiss() {
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    fun isShowing(): Boolean {
        if (progressDialog != null)
            return progressDialog!!.isShowing
        return false
    }
}