package com.cxm.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期操作工具类
 * 陈小默 16/9/18.
 */
object DateUtils {
    /**
     * 获得当前时间所在区间，今天，昨天，前天，本月，本年，以前
     */
    fun duration(time: Long): DateType {
        return if (time >= getDate(DateType.DAY).time) {
            DateType.TODAY
        } else if (time >= getDay().time - getLongTime(DateType.DAY)) {
            DateType.YESTERDAY
        } else if (time >= getDay().time - getLongTime(DateType.DAY) * 2) {
            DateType.BEFORE_YESTERDAY
        } else if (time >= getMonth().time) {
            DateType.THIS_MONTH
        } else if (time >= getYear().time) {
            DateType.THIS_YEAR
        } else {
            DateType.BEFORE_THIS_YEAR
        }
    }

    fun getDate(type: DateType): Date =
            when (type) {
                DateType.YEAR -> {
                    getYear()
                }
                DateType.MONTH -> {
                    getMonth()
                }
                DateType.DAY -> {
                    getDay()
                }
                else -> {
                    Date()
                }
            }

    /**
     * 获得字符串所表示的时间
     */
    fun getDate(time: String, pattern: String): Date = SimpleDateFormat(pattern).parse(time)

    /**
     * 获得今日子时时间
     */
    fun getDay(): Date {
        val p = "yyyy-MM-dd"
        val day = SimpleDateFormat(p).format(Date())
        return getDate(day, p)
    }

    fun getMonth(): Date {
        val p = "yyyy-MM"
        val day = SimpleDateFormat(p).format("${Date()}-01")
        return getDate(day, p)
    }

    fun getYear(): Date {
        val p = "yyyy"
        val day = SimpleDateFormat(p).format("${Date()}-01-01")
        return getDate(day, p)
    }

    /**
     * 获得以今天为准的的格式化日期
     */
    fun getFormatString(time: Long): String = getFormatString(Date(time))

    /**
     * 自定义格式化方式输出当前日期
     */
    fun getFormatString(date: Date, pattern: String): String = SimpleDateFormat(pattern).format(date)

    /**
     * 获得以今天为准的的格式化日期
     */
    fun getFormatString(date: Date): String =
            when (duration(date.time)) {
                DateType.TODAY -> {
                    getFormatString(date, "今天HH:mm")
                }
                DateType.YESTERDAY -> {
                    getFormatString(date, "昨天HH:mm")
                }
                DateType.BEFORE_YESTERDAY -> {
                    getFormatString(date, "前天HH:mm")
                }
                DateType.THIS_MONTH, DateType.THIS_YEAR -> {
                    getFormatString(date, "MM-dd HH:mm")
                }
                else -> {
                    getFormatString(date, "yyyy-MM-dd HH:mm")
                }
            }

    /**
     * 以2008-08-08 20:00:00格式输出时间
     */
    fun getSimpleDateString(date: Date): String = getFormatString(date, "yyyy-MM-dd HH:mm")

    /**
     * 以2008-08-08 20:00:00格式输出时间
     */
    fun getSimpleDateString(time: Long): String = getSimpleDateString(Date(time))

    fun getTimeStamp(): String = getFormatString(Date(), "yyyy-MM-dd-HH-mm-ss-SS")
    /**
     * 获得一个时间段的毫秒长度
     */
    fun getLongTime(type: DateType): Long {
        return when (type) {
            DateType.DAY -> 1000L * 60 * 60 * 24
            DateType.MONTH -> 1000L * 60 * 60 * 24 * 30
            DateType.YEAR -> 1000L * 60 * 60 * 24 * 365
            else -> 0
        }
    }

    /**
     * 获得年龄
     */
    fun getAge(time: Long): Int {
        val birth = Date(time)
        val now = Date()
        var age = getBirthYear(now) - getBirthYear(birth)
        if (getBirthMonth(birth) > getBirthMonth(now) || getBirthMonth(birth) === getBirthMonth(now) && getBirthDay(birth) > getBirthDay(now)) {
            age--
        }
        return age
    }

    /**
     * 获得出生年份的int值
     */
    fun getBirthYear(time: Date): Int {
        val simpleDateFormat = SimpleDateFormat("yyyy")
        return Integer.parseInt(simpleDateFormat.format(time))
    }

    /**
     * 获得出生月份的int值
     */
    fun getBirthMonth(time: Date): Int {
        val simpleDateFormat = SimpleDateFormat("MM")
        return Integer.parseInt(simpleDateFormat.format(time))
    }

    /**
     * 获得出生日期的int值
     */
    fun getBirthDay(time: Date): Int {
        val simpleDateFormat = SimpleDateFormat("dd")
        return Integer.parseInt(simpleDateFormat.format(time))
    }
}

enum class DateType {
    /**
     * 现在
     */
    NOW,
    YEAR, MONTH, DAY, HOUR,
    /**
     * 今天
     */
    TODAY,
    /**
     * 昨天
     */
    YESTERDAY,
    /**
     * 前天
     */
    BEFORE_YESTERDAY,
    /**
     * 本月
     */
    THIS_MONTH,
    /**
     * 今年
     */
    THIS_YEAR,
    /**
     * 很久以前
     */
    BEFORE_THIS_YEAR;
}