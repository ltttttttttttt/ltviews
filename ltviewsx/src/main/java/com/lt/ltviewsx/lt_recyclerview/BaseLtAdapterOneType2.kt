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
 *              useOldBackend = true//使用旧的kt后端,会使kae性能更好,但无法支持compose等kt新特性,所以建议使用BaseAdapterOneType
 *          }
 */

/**
 * 普通的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 * @param itemLayoutId 条目布局id,如果不写(等于0),则可以重写[createView]来生成条目的view
 */
@Deprecated("使用不带2的,kae即将废弃,所以改用viewBinding")
abstract class BaseAdapterOneType2<T>(
    val list: MutableList<T>,
    @LayoutRes private val itemLayoutId: Int = 0
) : RecyclerView.Adapter<BaseLtViewHolder2>() {
    abstract fun setData(h: BaseLtViewHolder2, b: T, i: Int)

    override fun onBindViewHolder(holder: BaseLtViewHolder2, position: Int) =
        try {
            setData(holder, list[position], position)
        } catch (t: Throwable) {
            LtRecyclerViewManager.onLtViewsCatchHandler(t)
            //如果没有对异常做正确处理,可能会显示异常
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder2 =
        try {
            if (itemLayoutId != 0)
                BaseLtViewHolder2(
                    LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
                )
            else
                BaseLtViewHolder2(createView(parent))
        } catch (t: Throwable) {
            LtRecyclerViewManager.onLtViewsCatchHandler(t)
            BaseLtViewHolder2(View(parent.context))//如果没有对异常做正确处理,可能会显示异常
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
@Deprecated("使用不带2的,kae即将废弃,所以改用viewBinding")
abstract class BaseLtAdapterOneType2<T>(
    val list: MutableList<T>,
    @LayoutRes private val itemLayoutId: Int = 0,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView()
) : LtAdapter<BaseLtViewHolder2>(view) {

    abstract fun setData(h: BaseLtViewHolder2, b: T, i: Int)

    override fun onLtBindViewHolder(holder: BaseLtViewHolder2, position: Int) =
        setData(holder, list[position], position)

    override fun getLtItemCount() = list.size

    override fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder2 =
        if (itemLayoutId != 0)
            BaseLtViewHolder2(
                LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
            )
        else
            BaseLtViewHolder2(createView(parent))

    open fun createView(parent: ViewGroup): View =
        throw RuntimeException("${this::class.simpleName}:请填写itemLayoutId或者重写createView()")
}

/**
 * 使用方便的ViewHolder
 * [LayoutContainer]使用kt的框架来快捷查找view,并且带有缓存
 */
@Deprecated("使用不带2的,kae即将废弃,所以改用viewBinding")
open class BaseLtViewHolder2(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    override val containerView: View?
        get() = itemView
}

/**
 * 快速创建adapter
 */
@Deprecated("使用不带2的,kae即将废弃,所以改用viewBinding")
inline fun <T> adapterOf2(
    list: MutableList<T>,
    @LayoutRes itemLayoutId: Int,
    crossinline setData: BaseAdapterOneType2<T>.(h: BaseLtViewHolder2, b: T, i: Int) -> Unit
)
        : BaseAdapterOneType2<T> =
    object : BaseAdapterOneType2<T>(list, itemLayoutId) {
        override fun setData(h: BaseLtViewHolder2, b: T, i: Int) =
            setData(this, h, b, i)
    }

/**
 * 快速创建ltAdapter
 */
@Deprecated("使用不带2的,kae即将废弃,所以改用viewBinding")
inline fun <T> ltAdapterOf2(
    list: MutableList<T>,
    @LayoutRes itemLayoutId: Int,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView(),
    crossinline setData: BaseLtAdapterOneType2<T>.(h: BaseLtViewHolder2, b: T, i: Int) -> Unit
)
        : BaseLtAdapterOneType2<T> =
    object : BaseLtAdapterOneType2<T>(list, itemLayoutId, view) {
        override fun setData(h: BaseLtViewHolder2, b: T, i: Int) =
            setData(this, h, b, i)
    }