package com.lt.ltviewsx.lt_recyclerview

import android.view.View
import android.view.ViewGroup

/**
 * 创    建:  lt  2018/5/23--17:02
 * 作    用:  刷新的布局
 * 注意事项:
 */
interface BaseRefreshLayout {
    fun setEnabled(enabled: Boolean) //设置下拉布局是否可用
    fun setOnRefreshListener(listener: Function0<Unit>?) //设置刷新的监听
    var isRefreshing: Boolean//设置和获取是否刷新
    fun setLayoutParams(params: ViewGroup.LayoutParams) //适配View
    fun addView(child: View) //适配View
}