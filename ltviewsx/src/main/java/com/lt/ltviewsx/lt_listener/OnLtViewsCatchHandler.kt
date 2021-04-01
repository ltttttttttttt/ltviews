package com.lt.ltviewsx.lt_listener

/**
 * creator: lt  2021/4/1  lt.dygzs@qq.com
 * effect :
 * warning:
 */
fun interface OnLtViewsCatchHandler {
    @Throws(Throwable::class)
    operator fun invoke(t: Throwable)
}