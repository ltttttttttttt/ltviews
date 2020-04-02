package com.lt.ltviewsx.utils

/**
 * creator: lt  2020/2/17  lt.dygzs@qq.com
 * effect : 工具类
 * warning:
 */
/**
 * 获取集合长度
 */
internal fun Collection<*>?.nullSize(): Int = this?.size ?: 0

/**
 * 如果是true返回前面的值,否则返回后面的值
 */
internal fun <T> Boolean?.yesOrNo(yes: T, no: T): T = if (this == true) yes else no