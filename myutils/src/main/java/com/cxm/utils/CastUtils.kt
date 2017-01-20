package com.cxm.utils

/**
 * 类型转换工具类
 * 陈小默 16/8/18.
 */
class CastUtils {
    companion object {
        fun castString(any: Any?): String? = any.toString()
        fun castString(any: Any?, defaultString: String): String {
            val result = castString(any)
            if (result != null) {
                return result
            } else {
                return defaultString
            }
        }

        fun castDouble(any: Any?, defaultDouble: Double): Double {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toDouble()
                } catch (e: Exception) {
                    return defaultDouble
                }
            } else {
                return defaultDouble
            }
        }

        fun castFloat(any: Any?, defaultFloat: Float): Float {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toFloat()
                } catch (e: Exception) {
                    return defaultFloat
                }
            } else {
                return defaultFloat
            }
        }

        fun castLong(any: Any?, defaultLong: Long): Long {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toLong()
                } catch (e: Exception) {
                    return defaultLong
                }
            } else {
                return defaultLong
            }
        }

        fun castInt(any: Any?, defaultInt: Int): Int {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toInt()
                } catch (e: Exception) {
                    return defaultInt
                }
            } else {
                return defaultInt
            }
        }

        fun castChar(any: Any?, defaultChar: Char): Char {
            val result = castString(any)
            if (result != null && result.length == 1) {
                try {
                    return result[0]
                } catch (e: Exception) {
                    return defaultChar
                }
            } else {
                return defaultChar
            }
        }

        fun castShort(any: Any?, defaultShort: Short): Short {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toShort()
                } catch (e: Exception) {
                    return defaultShort
                }
            } else {
                return defaultShort
            }
        }

        fun castByte(any: Any?, defaultByte: Byte): Byte {
            val result = castString(any)
            if (result != null) {
                try {
                    return result.toByte()
                } catch (e: Exception) {
                    return defaultByte
                }
            } else {
                return defaultByte
            }
        }

        fun castBoolean(any: Any?, defaultBoolean: Boolean): Boolean =
                try {
                    val result = castString(any)
                    if (result != null) {
                        result.toBoolean()
                    } else {
                        defaultBoolean
                    }
                } catch (e: Exception) {
                    defaultBoolean
                }
    }
}