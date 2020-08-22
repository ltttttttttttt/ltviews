package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * 创    建:  lt  2018/3/9--11:22
 * 作    用:  LtAdapter和Adapter的Base
 * 注意事项:  只能在有一种type的情况下使用
 */

/**
 * 普通的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 * @param itemLayoutId 条目布局id,如果不写(等于0),则可以重写[createView]来生成条目的view
 */
abstract class BaseAdapterOneType<T>(val list: MutableList<T>,
                                     @LayoutRes private val itemLayoutId: Int = 0)
    : RecyclerView.Adapter<BaseLtViewHolder>() {
    abstract fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder)

    override fun onBindViewHolder(holder: BaseLtViewHolder, position: Int) = setData(holder.viewFind, list[position], position, holder)

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder =
            if (itemLayoutId != 0)
                BaseLtViewHolder(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false))
            else
                BaseLtViewHolder(createView(parent))

    open fun createView(parent: ViewGroup): View = throw RuntimeException("请填写itemLayoutId或者重写createView()")
}

/**
 * LtAdapter的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 * @param itemLayoutId 条目布局id,如果不写(等于0),则可以重写[createView]来生成条目的view
 */
abstract class BaseLtAdapterOneType<T>(val list: MutableList<T>,
                                       @LayoutRes private val itemLayoutId: Int = 0,
                                       view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView())
    : LtAdapter<BaseLtViewHolder>(view) {

    abstract fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder)

    override fun onLtBindViewHolder(holder: BaseLtViewHolder, position: Int) = setData(holder.viewFind, list[position], position, holder)

    override fun getLtItemCount() = list.size

    override fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder =
            if (itemLayoutId != 0)
                BaseLtViewHolder(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false))
            else
                BaseLtViewHolder(createView(parent))

    open fun createView(parent: ViewGroup): View = throw RuntimeException("请填写itemLayoutId或者重写createView()")
}

/**
 * 使用方便的ViewHolder
 */
open class BaseLtViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val viewFind: ViewFind = ViewFind().apply { this.view = this@BaseLtViewHolder.view }
}

/**
 * 使用kt的框架来快捷查找view,并且带有缓存
 */
open class ViewFind : Fragment() {
    private lateinit var mView: View

    fun setView(view: View) {
        this.mView = view
    }

    override fun getView(): View = mView

    override fun getContext(): Context = mView.context
}

/**
 * 快速创建adapter
 */
inline fun <T> adapterOf(list: MutableList<T>,
                         @LayoutRes itemLayoutId: Int,
                         crossinline setData: BaseAdapterOneType<T>.(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) -> Unit)
        : BaseAdapterOneType<T> =
        object : BaseAdapterOneType<T>(list, itemLayoutId) {
            override fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) = setData(this, v, b, i, h)
        }

/**
 * 快速创建ltAdapter
 */
inline fun <T> ltAdapterOf(list: MutableList<T>,
                           @LayoutRes itemLayoutId: Int,
                           view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView(),
                           crossinline setData: BaseLtAdapterOneType<T>.(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) -> Unit)
        : BaseLtAdapterOneType<T> =
        object : BaseLtAdapterOneType<T>(list, itemLayoutId, view) {
            override fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) = setData(this, v, b, i, h)
        }