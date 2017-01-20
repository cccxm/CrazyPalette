package com.cxm.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.cxm.bind.AbsViewHolder
import java.util.*

abstract class CommonBaseAdapter<T, H : AbsViewHolder>(val holder: Class<H>,
                                                       val resId: Int,
                                                       val context: Context) : BaseAdapter() {
    var list: MutableList<T> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun addData(data: T) {
        list.add(data)
        notifyDataSetChanged()
    }

    fun addAllData(list: MutableList<T>) {
        list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder = AbsViewHolder.getViewHolder(holder, convertView, LayoutInflater.from(context), resId)
        viewHolder(holder, getItem(position), position)
        return holder.mConvertView!!
    }

    override fun getItem(position: Int): T = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = list.size

    abstract fun viewHolder(holder: H, item: T, position: Int)
}
