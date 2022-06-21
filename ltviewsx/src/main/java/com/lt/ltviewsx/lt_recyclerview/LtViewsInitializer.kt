package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import androidx.startup.Initializer

/**
 * creator: lt  2022/6/21  lt.dygzs@qq.com
 * effect : 使用start up初始化context
 * warning: 参考: https://www.jianshu.com/p/98d3623fa2d4
 */
class LtViewsInitializer : Initializer<LtRecyclerViewManager> {
    override fun create(context: Context): LtRecyclerViewManager {
        return LtRecyclerViewManager.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}