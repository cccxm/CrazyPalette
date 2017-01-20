package com.cxm.file

import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * 文本文件工具类。其中使用的文件流为
 * <tt>RandomAccessFile<tt>类，该类操作以字符为基础的文本文件， 可以向操作字符串一样操作文本中的字符数据。
 * 当前版本操作中文字符可能会出现未知异常。
 * @author 陈小默 16/8/22.
 */
class FileUtils {
    private var regex = "[^a-zA-Z0-9]+"
    private var file: File? = null
    private var ran: RandomAccessFile? = null
    private var index: LinkedList<Int>? = null
    private var wordCount: Int = 0
    private var words: Array<String>? = null
    private var lineCont: Int = 0
    private var sumLineCount: Int = 0
    private var lines: Array<String>? = null
    private var sumWordCount: Int = 0

    constructor(file: File) {
        this.file = file
        ran = RandomAccessFile(file, "rw")
        index = LinkedList<Int>()
    }

    constructor(dirName: String, fileName: String) {
        file = getFile(dirName, fileName)
        ran = RandomAccessFile(file, "rw")
        index = LinkedList<Int>()
    }

    companion object {
        /**
         * 以字符串格式返回文本中的数据
         * @since 15.8.8
         */
        @JvmStatic fun toString(file: File) = FileUtils(file).getContent()

        /**
         * 创建文件对象
         * @throws IOException
         */
        @JvmStatic fun getFile(dirName: String, fileName: String): File {
            val directory = File(dirName)
            if (!directory.exists())
                directory.mkdirs()
            val file = File(directory, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        }
    }

    /**
     * 查询文本中与字符串相匹配的索引,并以数组形式返回
     * @throws IOException
     */
    fun search(str: String): IntArray {
        var content: String?
        var site = 0
        var n: Int
        while (site < ran!!.length().toInt()) {
            content = getContent(site, ran!!.length().toInt())
            n = content.indexOf(str)
            if (n == -1)
                break
            site += n
            index!!.add(site)
            site += str.length
        }
        if (index!!.size == 0)
            return IntArray(0)
        val a = IntArray(index!!.size)
        site = 0
        for (obj in index!!.toTypedArray()) {
            a[site++] = obj as Int
        }
        return a
    }

    /**
     * 按单词遍历文本文件
     * @throws IOException
     */
    fun nextWord(): String = words!![words!!.size - wordCount--]


    /**
     * 从当前单词位置向后查询len个单词并返回
     * @throws IOException
     */
    fun nextWord(len: Int): String {
        wordCount -= len
        return words!![words!!.size - wordCount--]
    }

    /**
     * 查找文本中是否包含此单词，包含则返回位置，否则返回-1；
     * 查找到单词后可以调用nextWord方法，由此遍历。若第一次使用此方法或文本已修改，建议先调用reset方法
     * @throws IOException
     */
    fun findWord(word: String): Int {
        if (words == null)
            words = getContent().split(regex.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in words!!.indices) {
            if (words!![i] == word) {
                wordCount = words!!.size - i
                return i
            }
        }
        return -1
    }

    /**
     * 当前是否有下一行
     * @throws IOException
     */
    fun hasNextLine(): Boolean {
        if (lines == null) {
            lines = getContent().split("[\r\n]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            lineCont = lines!!.size
            sumLineCount = lineCont
        }
        return lineCont != 0
    }

    /**
     * 获取文件有效行数
     */
    fun getLineCount(): Int {
        hasNextLine()
        return sumLineCount
    }

    /**
     * 读取下一行数据
     */
    fun nextLine(): String = lines!![lines!!.size - lineCont--]

    /**
     * 是否还有单词未遍历
     */
    operator fun hasNext(): Boolean {
        if (words == null) {
            words = getContent().split(regex.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            wordCount = words!!.size
            sumWordCount = wordCount
        }
        return wordCount != 0
    }

    /**
     * 获取文本文件中的单词数
     */
    fun getWordCount(): Int {
        hasNext()
        return sumWordCount
    }

    /**
     * 重置单词遍历器，当文本被修改后应当调用此方法。
     * @throws IOException
     */
    fun reset() {
        wordCount = 0
        words = null
        lineCont = 0
        lines = null
    }

    /**
     * 重置单词遍历器，并设置单次分割方式。
     * @throws IOException
     */
    fun reset(regex: String) {
        wordCount = 0
        words = null
        this.regex = regex
        lineCont = 0
        lines = null
    }

    /**
     * 删除本对象当前持有的文件，并关闭所有的流
     * @throws IOException
     */
    fun delete(): Boolean {
        ran!!.close()
        return file!!.delete()
    }

    /**
     * 关闭当前操作文件的随机流
     * @throws IOException
     */
    fun close() {
        ran!!.close()
    }

    /**
     * 获取全部文本内容
     * @throws IOException
     */
    fun getContent(): String = getContent(0, ran!!.length().toInt())

    /**
     * 从当前文件中获取输入流
     * @throws IOException
     */
    fun getInputStream(): InputStream = FileInputStream(file!!)

    /**
     * 获取某一区间的文本数据
     * @throws IOException
     */
    fun getContent(start: Int, end: Int): String {
        val buff = ByteArray(end - start)
        ran!!.seek(start.toLong())
        return String(buff, 0, ran!!.read(buff), Charset.forName("utf-8"))
    }

    /**
     * 使用目标字符串替换源字符串,因为此方法算法错位倾向,现在被newReplace方法替换
     * @throws IOException
     */
    @Deprecated("此方法具有错位倾向,现在被newReplace方法替换")
    fun replace(source: String, target: String): Boolean {
        val len = source.length
        val size = target.length - len
        val site = search(source)
        var i = 0
        if (site.size == 0)
            return false
        for (a in site) {
            replace(target, a + size * i, a + size * i++ + len)
        }
        return true
    }

    /**
     * 使用目标字符串替换源字符串
     * @throws IOException
     */
    fun newReplace(source: String, target: String): Boolean {
        val text = getContent()
        return if (!text.contains(source))
            false
        else {
            val str = text.split(source.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val content = StringBuilder()
            for (i in 0..str.size - 1 - 1) {
                content.append(str[i]).append(target)
            }
            content.append(str[str.size - 1])
            clear()
            append(content.toString())
            true
        }
    }

    /**
     * 使用content替换区间内数据
     * @throws IOException
     */
    fun replace(content: String, start: Int, end: Int) {
        val str = getContent(0, start) + content + getContent(end, ran!!.length().toInt())
        clear()
        ran!!.write(str.toByteArray(charset("utf-8")))
    }

    /**
     * 删除区间内数据
     */
    fun remove(start: Int, end: Int): String {
        val str = getContent(start, end)
        replace("", start, end)
        return str
    }

    /**
     * 删除文件中对应的文本数据
     * @throws IOException
     */
    fun remove(str: String): Boolean = newReplace(str, "")

    /**
     * 获取文件长度
     * @throws IOException
     */
    fun length(): Long = ran!!.length()

    /**
     * 插入文本数据
     * @throws IOException
     */
    fun insert(content: String, position: Int): Unit = replace(content, position, position)

    /**
     * 清空文件内容
     * @throws IOException
     */
    fun clear(): Unit = ran!!.setLength(0L)

    /**
     * 向文件末尾追加内容
     * @throws IOException
     */
    fun append(content: String) {
        val len = ran!!.length()
        ran!!.seek(len)
        // ran.writeUTF(content);
        ran!!.write(content.toByteArray(charset("utf-8")))
    }

    /**
     * 向文本中追加字节数据
     * @throws IOException
     */
    fun append(buff: ByteArray) {
        val len = ran!!.length()
        ran!!.seek(if (len == 0L) 0 else len - 1)
        // ran.writeUTF(content);
        ran!!.write(buff)
    }

    /**
     * 向文本中追加字节数据
     * @throws IOException
     */
    fun append(buff: ByteArray, offset: Int, len: Int) {
        val length = ran!!.length()
        ran!!.seek(if (length == 0L) 0 else length - 1)
        // ran.writeUTF(content);
        ran!!.write(buff, offset, len)
    }

    /**
     * 输出换行
     * @throws IOException
     */
    fun newLine(): Unit = append("\r\n")

    /**
     * 返回当前正在使用的文件对象
     */
    fun getFile(): File = file ?: throw FileNotFoundException()
}