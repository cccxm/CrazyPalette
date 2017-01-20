package com.cxm.utils

import android.content.Context

/**
 * 单位转换工具类
 * 陈小默 16/8/19.
 */
object UnitUtils {
    fun px2dip(pxValue: Int, context: Context) = pxValue / density(context) + 0.5

    fun dip2px(dipValue: Float, context: Context) = (density(context) * dipValue + 0.5F).toInt()

    fun density(context: Context) = context.resources.displayMetrics.density

    /**
     * 将长度格式化为标准形式1GB2MB3KB4B
     */
    fun formatSize(bit: Long): String {
        if (bit < 0) throw RuntimeException("bit must more than 0")
        var mBit = bit
        val unitArray = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB")
        val builder = StringBuilder()
        for (i in 0..unitArray.size - 1) {
            val num = mBit % 1024L
            if (num != 0L) builder.insert(0, unitArray[i]).insert(0, num)
            mBit /= 1024
            if (mBit == 0L) break
        }
        return builder.toString()
    }

    /**
     * 将长度格式化为浮点数形式 23.123GB
     */
    fun formatSizeFloat(bit: Long, size: Int = 3, fixed: Boolean = false): String {
        if (bit < 0) throw RuntimeException("bit must more than 0")
        if (size < 0) throw RuntimeException("size must more than 0")
        var mBit = bit
        val mSize = if (size > 5) 5 else size
        val unitArray = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB")
        var unit = unitArray[0]
        var preNum = bit
        var base = 1L
        val builder = StringBuilder()
        for (i in 0..unitArray.size - 1) {
            val num = mBit / 1024L
            if (num == 0L) break
            mBit /= 1024L
            preNum = num
            base *= 1024L
            unit = unitArray[i]
        }
        builder.append(preNum)
        if (size > 0) {
            val remain: Long = bit % base
            var f = remain.toDouble() / base.toDouble()
            var i = 0
            var baseInt = 1
            while (i++ < mSize) {
                baseInt *= 10
            }
            f *= baseInt
            var dec = f.toInt()
            if (dec != 0) {
                if (!fixed) while (dec % 10 == 0) {
                    dec /= 10
                }
                builder.append('.').append(dec)
            } else if (fixed) {
                builder.append('.')
                for (index in 0..mSize - 1) {
                    builder.append(0)
                }
            }
        }
        return builder.append(unit).toString()
    }
}