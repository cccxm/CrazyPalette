package com.cxm.utils

import java.util.*
import java.util.regex.Pattern

/**
 * 字符串操作工具類
 * 陈小默 16/8/29.
 */
object StringUtils {
    /**
     * 当前字符串是否为手机号
     */
    fun isPhone(str: String?): Boolean =
            if (str == null)
                false
            else Pattern.compile("1[0-9]{10}").matcher(str).matches()


    /**
     * 验证密码格式
     */
    fun password(password: String): Boolean = Pattern.compile("[\\w]{6,18}").matcher(password).matches()

    /**
     * 替换字符串中的符号
     */
    fun replaceAll(str: String, regex: String, replacement: String): String? {
        if (isEmpty(str))
            return null
        return str.trim { it <= ' ' }.replace(regex.toRegex(), replacement)
    }

    /**
     * 替换查询参数字符串
     */
    fun buildQueryString(str: String): String = "%" + replaceAll(str, "[ ]+", "%") + "%"

    /**
     * 是否包含数字
     */
    fun isContainNumber(string: String): Boolean = Pattern.compile("[\\w]*[0-9]+[\\w]*").matcher(string).matches()

    /**
     * 是否全为数字
     */
    fun isAllNumber(string: String): Boolean = Pattern.compile("[0-9]{1,}").matcher(string).matches()

    /**
     * 是否不含非法字符
     */
    fun isIllegalChar(string: String): Boolean = Pattern.compile("[0-9a-zA-Z\\-_\u4e00-\u9fa5]{1,}").matcher(string).matches()

    /**
     * 判断字符串是否为空对象
     */
    fun isNull(str: String?): Boolean = str == null

    fun allIsNull(vararg str: String): Boolean {
        for (s in str) {
            if (!isNull(s))
                return false
        }
        return true
    }

    /**
     * 判断字符串是否为空字符串
     */
    fun isEmpty(str: String): Boolean = if (isNull(str)) true else str.length == 0

    fun allIsEmpty(vararg str: String): Boolean {
        for (s in str) {
            if (!isEmpty(s))
                return false
        }
        return true
    }

    /**
     * 判断字符串是否为空白字符串
     */
    fun isBlank(str: String): Boolean = if (isEmpty(str)) true else str.trim { it <= ' ' }.length == 0

    fun allIsBlank(vararg str: String): Boolean {
        for (s in str) {
            if (!isBlank(s))
                return false
        }
        return true
    }

    fun isNotBlank(str: String): Boolean = !isBlank(str)

    fun allIsNotBlank(vararg str: String): Boolean {
        for (s in str) {
            if (!isNotBlank(s))
                return false
        }
        return true
    }

    /**
     * 判断长度
     */
    fun length(string: String, start: Int, end: Int): Boolean = string.length >= start && string.length <= end

    /**
     * 截取区间
     */
    fun subString(string: String, start: String,
                  end: String, length: Int): List<String>? {
        val index_start = getIndex(string, start)
        val index_end = getIndex(string, end)
        val strings = ArrayList<String>()
        if (index_start.size == 0 || index_end.size == 0)
            return strings
        val set = HashSet<String>()
        var i = 0
        var j = 0
        while (i < index_start.size && j < index_end.size) {
            j = 0
            while (j < index_end.size) {
                if (index_end[j] <= index_start[i]) {
                    j++
                    continue
                }
                if (index_end[j] - length > index_start[i]) {
                    if (i < index_start.size - 1 && index_end[j] > index_start[i + 1])
                        break
                    set.add(string.substring(index_start[i] + 1,
                            index_end[j]))
                    break
                }
                break
            }
            i++
        }
        strings.addAll(set)
        return strings
    }

    /**
     * 截取regex之间的字符串
     */
    fun subString(string: String, regex: String): List<String> {
        val index = getIndex(string, regex)
        val result = ArrayList<String>()
        if (index.size >= 2) {
            val set = HashSet<String>()
            var i = 0
            while (i < index.size) {
                if (i == index.size - 1)
                    break
                set.add(string.substring(index[i++] + regex.length,
                        index[i]))
                i++
            }
            result.addAll(set)
        }
        return result
    }

    /**
     * 获得字符串中字符串出现的所有位置
     */
    fun getIndex(string: String, regex: String): List<Int> {
        val indexList = ArrayList<Int>()
        val length = regex.length - 1
        val charArray = string.toCharArray()
        var i = 0
        while (i < charArray.size) {
            if (i == charArray.size - 1)
                break
            val indexOf = String(charArray, i, charArray.size - i).indexOf(regex)
            if (indexOf == -1)
                break
            indexList.add(indexOf + i)
            i += indexOf + length
            i++
        }
        return indexList
    }
}