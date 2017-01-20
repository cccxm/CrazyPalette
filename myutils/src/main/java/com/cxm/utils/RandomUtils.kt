package com.cxm.utils

import java.util.*

/**
 * 随机数工具类
 * 陈小默 16/9/18.
 */
object RandomUtils {
    private val MIN_CHINESE_UNICODE = 0x4E00
    private val MAX_CHINESE_UNICODE = 0x9FA5

    /**
     * 获取指定长度的随机字符串,字符串取值范围0-9a-zA-Z共62个取值
     */
    fun getString(length: Int): String {
        if (length < 0)
            throw IllegalArgumentException("$length :less zero")
        val builder = StringBuilder()
        for (i in 0..length - 1) {
            var c = '0'
            val ran = (Math.random() * 62).toInt()
            if (ran < 10)
                c = ('0' + ran).toChar()
            else if (ran >= 10 && ran < 36)
                c = ('a' + ran - 10).toChar()
            else if (ran >= 36)
                c = ('A' + ran - 36).toChar()
            builder.append(c)
        }
        return builder.toString()
    }

    /**
     * 获取唯一字符
     */
    fun getString(): String = UUID.randomUUID().toString()

    /**
     * 获取随机中文字符串
     */
    fun getChinese(length: Int): String {
        if (length < 0)
            throw IllegalArgumentException("$length :less zero")
        val builder = StringBuilder()
        for (i in 0..length - 1) {
            val c = getInt(MIN_CHINESE_UNICODE, MAX_CHINESE_UNICODE).toChar()
            builder.append(c)
        }
        return builder.toString()
    }

    /**
     * 获取指定长度的随机数字字符串，取值范围0-9
     */
    fun getNumberString(length: Int): String = "${getNumber(length)}"

    /**
     * 获得制定长度的整型数字
     */
    fun getNumber(length: Int): Int {
        if (length < 0)
            throw RuntimeException("$length :less zero")
        val start = Math.pow(10.0, (length - 1).toDouble()).toInt() - 1
        val end = Math.pow(10.0, length.toDouble()).toInt()
        return getInt(start, end)
    }

    /**
     * 获得区间内的整型随机数
     */
    fun getInt(start: Int, end: Int): Int = (start + Math.random() * (end - start)).toInt()

    /**
     * 获得区间内的浮点型随机数
     */
    fun getFloat(start: Float, end: Float): Float = (start + Math.random() * (end - start)).toFloat()

    /**
     * 获得区间内的双精度浮点型随机数
     */
    fun getDouble(start: Double, end: Double): Double = start + Math.random() * (end - start)

}