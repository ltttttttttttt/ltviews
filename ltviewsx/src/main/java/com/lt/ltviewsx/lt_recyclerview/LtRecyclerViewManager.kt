package com.lt.ltviewsx.lt_recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.lt.ltviewsx.R

/**
 * 创    建:  lt  2018/5/22--15:23
 * 作    用:  ltRv的管理者
 * 注意事项:
 */
@SuppressLint("StaticFieldLeak")
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
     * 但是因为使用了Application的上下文,所以用该上下文的view无法使用配置好的主题,或设置好的density(一般上面两种情况也没什么问题),但是可以通过在Adapter的构造中传入自己new的view来避免
     */
    var context: Context? = null

    /**
     * 下拉刷新的View的构造方法引用
     */
    var refreshLayoutConstructorFunction: (Context, AttributeSet?, Int) -> BaseRefreshLayout = ::MTextRefreshLayout

    /**
     * 上拉加载的布局id
     */
    var upLayoutId = R.layout.lt_up_loading

    /**
     * 下拉阈值
     */
    var refreshThreshold = 0F

    /**
     * 下拉时RecyclerView是否跟着向下移动(仅自定义下拉刷新布局)
     */
    var isRvIsMove = true

    /**
     * 上拉已经没数据了,再次上拉是否加载数据
     */
    var isNoDataIsLoad = false

    /**
     * noItemTextView的字体颜色
     */
    var noItemTextColor = 0

    /**
     * 表示外部引用是否是Debug状态,如果是Debug状态,则不帮外部cache某些异常
     */
    var isDebug = false

    /**
     * 获取默认(自定义设置)的适配器底部刷新view
     */
    fun getDefaultBottomRefreshView(): View = View.inflate(context, upLayoutId, null)

    /**
     * Adapter的回调方法中出现了异常的处理方式,默认抛异常,用户可以自行捕获相应异常
     */
    var onAdapterCatchHandler: (Throwable) -> Unit = { throw it }
}