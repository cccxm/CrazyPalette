package com.cxm.bind

import android.app.Activity
import android.view.LayoutInflater
import android.view.View

/**
 * 提供視圖綁定和銷毀的方法
 * @author 陈小默 16/8/17.
 */
abstract class AbsViewHolder {
    var mConvertView: View? = null
        private set

    /**
     * @author cxm
     * 在Activity中綁定視圖
     */
    fun bind(activity: Activity) {
        val any = this
        any.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val ann = field.getAnnotation(ViewInject::class.java)
            if (ann != null) {
                val view = activity.findViewById(ann.value)
                field.set(any, view)
            }
        }
    }

    /**
     * @author cxm
     * 其他條件下,比如Fragment中的視圖綁定
     */
    fun bind(root: View) {
        val any = this
        any.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val ann = field.getAnnotation(ViewInject::class.java)
            if (ann != null) {
                val view = root.findViewById(ann.value)
                field.set(any, view)
            }
        }
    }

    companion object {
        /**
         * @author cxm
         * ListView中可以使用此靜態方法創建一個ViewHolder對象.從返回的ViewHolder對象中可以獲得convertView
         */
        @JvmStatic fun <H : AbsViewHolder> getViewHolder(holder: Class<H>, convertView: View?, inflater: LayoutInflater, layoutResId: Int): H =
                if (convertView == null || convertView.tag == null) {
                    val viewHolder = holder.newInstance()
                    val view = inflater.inflate(layoutResId, null)
                    view.tag = viewHolder
                    viewHolder.mConvertView = view
                    viewHolder.bind(view)
                    viewHolder
                } else convertView.tag!! as H
    }

    /**
     * @author cxm
     * 取消綁定,銷毀視圖
     */
    fun unBind() {
        val any = this
        any.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            field.set(any, null)
        }
    }
}