package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lt.ltviewsx.R

/**
 * 创    建:  lt  2018/5/23--17:52
 * 作    用:  兼容的下拉刷新控件
 * 注意事项:
 */
class MSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : SwipeRefreshLayout(context, attrs), BaseRefreshLayout {
    init {
        setColorSchemeResources(R.color.colorAccent)
    }
}