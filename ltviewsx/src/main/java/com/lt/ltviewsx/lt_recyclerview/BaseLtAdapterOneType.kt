package com.lt.ltviewsx.lt_recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * 创    建:  lt  2018/3/9--11:22
 * 作    用:  LtAdapter和Adapter的Base
 * 注意事项:  只能在有一种type的情况下使用
 * 使用该种类的适配器,需要在对应的model的gradle的android{}中加入下面代码
 *          androidExtensions {
 *              experimental = true//启用自定义的kae
 *          }
 *          kotlinOptions {
 *              jvmTarget = "1.8"
 *              useOldBackend = true//使用旧的kt后端,会使kae性能更好,但无法支持compose等kt新特性,所以建议使用 TODO XxxAdapter(View or Data Binding)
 *          }
 */

/**
 * 普通的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 * @param itemLayoutId 条目布局id,如果不写(等于0),则可以重写[createView]来生成条目的view
 */
abstract class BaseAdapterOneType<T>(
    val list: MutableList<T>,
    @LayoutRes private val itemLayoutId: Int = 0
) : RecyclerView.Adapter<BaseLtViewHolder>() {
    abstract fun setData(h: BaseLtViewHolder, b: T, i: Int)

    override fun onBindViewHolder(holder: BaseLtViewHolder, position: Int) =
        try {
            setData(holder, list[position], position)
        } catch (t: Throwable) {
            LtRecyclerViewManager.onLtViewsCatchHandler(t)
            //如果没有对异常做正确处理,可能会显示异常
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder =
        try {
            if (itemLayoutId != 0)
                BaseLtViewHolder(
                    LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
                )
            else
                BaseLtViewHolder(createView(parent))
        } catch (t: Throwable) {
            LtRecyclerViewManager.onLtViewsCatchHandler(t)
            BaseLtViewHolder(View(parent.context))//如果没有对异常做正确处理,可能会显示异常
        }

    open fun createView(parent: ViewGroup): View =
        throw RuntimeException("${this::class.simpleName}:请填写itemLayoutId或者重写createView()")
}

/**
 * LtAdapter的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 * @param itemLayoutId 条目布局id,如果不写(等于0),则可以重写[createView]来生成条目的view
 */
abstract class BaseLtAdapterOneType<T>(
    val list: MutableList<T>,
    @LayoutRes private val itemLayoutId: Int = 0,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView()
) : LtAdapter<BaseLtViewHolder>(view) {

    abstract fun setData(h: BaseLtViewHolder, b: T, i: Int)

    override fun onLtBindViewHolder(holder: BaseLtViewHolder, position: Int) =
        setData(holder, list[position], position)

    override fun getLtItemCount() = list.size

    override fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder =
        if (itemLayoutId != 0)
            BaseLtViewHolder(
                LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
            )
        else
            BaseLtViewHolder(createView(parent))

    open fun createView(parent: ViewGroup): View =
        throw RuntimeException("${this::class.simpleName}:请填写itemLayoutId或者重写createView()")
}

/**
 * 使用方便的ViewHolder
 * [LayoutContainer]使用kt的框架来快捷查找view,并且带有缓存
 */
open class BaseLtViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    override val containerView: View?
        get() = itemView
}

/**
 * 快速创建adapter
 */
inline fun <T> adapterOf(
    list: MutableList<T>,
    @LayoutRes itemLayoutId: Int,
    crossinline setData: BaseAdapterOneType<T>.(h: BaseLtViewHolder, b: T, i: Int) -> Unit
)
        : BaseAdapterOneType<T> =
    object : BaseAdapterOneType<T>(list, itemLayoutId) {
        override fun setData(h: BaseLtViewHolder, b: T, i: Int) =
            setData(this, h, b, i)
    }

/**
 * 快速创建ltAdapter
 */
inline fun <T> ltAdapterOf(
    list: MutableList<T>,
    @LayoutRes itemLayoutId: Int,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView(),
    crossinline setData: BaseLtAdapterOneType<T>.(h: BaseLtViewHolder, b: T, i: Int) -> Unit
)
        : BaseLtAdapterOneType<T> =
    object : BaseLtAdapterOneType<T>(list, itemLayoutId, view) {
        override fun setData(h: BaseLtViewHolder, b: T, i: Int) =
            setData(this, h, b, i)
    }