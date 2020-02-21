package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.lt.ltviewsx.R

/**
 * 创    建:  lt  2018/5/22--15:23
 * 作    用:  ltRv的管理者
 * 注意事项:
 */
object LtRecyclerViewManager {
    /**
     * 初始化,在application的onCreate中调用
     */
    fun init(context: Context): LtRecyclerViewManager {
        this.context = context
        refreshThreshold = 80 * context.resources.displayMetrics.density
        noItemTextColor = context.resources.getColor(R.color.color_333)
        return this
    }

    /**
     * 创建刷新布局时的上下文,必须有
     */
    var context: Context? = null
        private set
    /**
     * 下拉刷新的View的class
     */
    var refreshLayoutClazz: Class<*> = MSwipeRefreshLayout::class.java
        private set
    /**
     * 上拉加载的布局id
     */
    var upLayoutId = R.layout.lt_up_loading
        private set
    /**
     * 下拉阈值
     */
    var refreshThreshold = 0F
        private set
    /**
     * 下拉时RecyclerView是否跟着向下移动(仅自定义下拉刷新布局)
     */
    var isRvIsMove = true
        private set
    /**
     * 上拉已经没数据了,再次上拉是否加载数据
     */
    var isNoDataIsLoad = false
        private set
    /**
     * noItemTextView的字体颜色
     */
    var noItemTextColor = 0
        private set
    internal val handler by lazy { Handler(Looper.getMainLooper()) }//项目内用的handler

    /**
     * 获取默认(自定义设置)的适配器底部刷新view
     */
    fun getDefualtBottomRefreshView(): View = View.inflate(context, upLayoutId, null)

    fun setContext(context: Context): LtRecyclerViewManager {
        this.context = context
        return this
    }

    fun setRefreshLayoutClazz(refreshLayoutClazz: Class<*>): LtRecyclerViewManager {
        this.refreshLayoutClazz = refreshLayoutClazz
        return this
    }

    fun setUpLayoutId(upLayoutId: Int): LtRecyclerViewManager {
        this.upLayoutId = upLayoutId
        return this
    }

    fun setRefreshThreshold(refreshThreshold: Float): LtRecyclerViewManager {
        this.refreshThreshold = refreshThreshold
        return this
    }

    fun setIsRvIsMove(isRvIsMove: Boolean): LtRecyclerViewManager {
        this.isRvIsMove = isRvIsMove
        return this
    }

    fun setIsNoDataIsLoad(isNoDataIsLoad: Boolean): LtRecyclerViewManager {
        this.isNoDataIsLoad = isNoDataIsLoad
        return this
    }

    fun setNoItemTextColor(noItemTextColor: Int): LtRecyclerViewManager {
        this.noItemTextColor = noItemTextColor
        return this
    }
}