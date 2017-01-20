package com.cxm.utils

import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * 属性文件操作工具类
 * 陈小默 16/8/18.
 */
class PropertyUtils {
    private val properties: Properties

    constructor(file: File) {
        properties = loadProps(file)
    }

    private fun loadProps(file: File): Properties {
        var properties = Properties()
        properties.load(FileInputStream(file))
        return properties
    }

    fun getString(key: String, defaultValue: String) = properties.getProperty(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean) = CastUtils.castBoolean(properties.getProperty(key), defaultValue)

    fun getByte(key: String, defaultValue: Byte) = CastUtils.castByte(properties.getProperty(key), defaultValue)

    fun getShort(key: String, defaultValue: Short) = CastUtils.castShort(properties.getProperty(key), defaultValue)

    fun getChar(key: String, defaultValue: Char) = CastUtils.castChar(properties.getProperty(key), defaultValue)

    fun getInt(key: String, defaultValue: Int) = CastUtils.castInt(properties.getProperty(key), defaultValue)

    fun getLong(key: String, defaultValue: Long) = CastUtils.castLong(properties.getProperty(key), defaultValue)

    fun getFloat(key: String, defaultValue: Float) = CastUtils.castFloat(properties.getProperty(key), defaultValue)

    fun getDouble(key: String, defaultValue: Double) = CastUtils.castDouble(properties.getProperty(key), defaultValue)
}