package site.lilpig.gayhub.utils

import android.webkit.URLUtil
import java.net.URL
import java.util.regex.Pattern


fun String.similarity(target: String): Int {
    val str = this
    val d: Array<IntArray>              // 矩阵
    val n = str.length
    val m = target.length
    var i: Int                  // 遍历str的
    var j: Int                  // 遍历target的
    var ch1: Char               // str的
    var ch2: Char               // target的
    var temp: Int               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
    if (n == 0) {
        return m
    }
    if (m == 0) {
        return n
    }
    d = Array(n + 1) { IntArray(m + 1) }
    // 初始化第一列
    i = 0
    while (i <= n) {
        d[i][0] = i
        i++
    }
    // 初始化第一行
    j = 0
    while (j <= m) {
        d[0][j] = j
        j++
    }
    i = 1
    while (i <= n) {
        // 遍历str
        ch1 = str[i - 1]
        // 去匹配target
        j = 1
        while (j <= m) {
            ch2 = target[j - 1]
            if (ch1 == ch2 || ch1.toInt() == ch2.toInt() + 32 || ch1.toInt() + 32 == ch2.toInt()) {
                temp = 0
            } else {
                temp = 1
            }
            // 左边+1,上边+1, 左上角+temp取最小
            d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp)
            j++
        }
        i++
    }
    return d[n][m]
}


/**
 * 获取最小的值
 */
private fun min(one: Int, two: Int, three: Int): Int {
    var one = if(one<two) one else two
    return if(one<three) one else three
}

fun String.isMatches(regex: String): Boolean{
    val pattern = Pattern.compile(regex)
    return pattern.matcher(this).matches()
}

