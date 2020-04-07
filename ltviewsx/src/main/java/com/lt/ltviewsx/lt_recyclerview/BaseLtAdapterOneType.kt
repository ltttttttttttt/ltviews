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
 * @param VH ViewHolder的泛型
 */
abstract class BaseAdapterOneType<T, VH : RecyclerView.ViewHolder>(val list: MutableList<T>)
    : RecyclerView.Adapter<VH>() {
    /**
     * 给view设置数据
     */
    abstract fun setData(b: T, i: Int, h: VH)

    override fun onBindViewHolder(holder: VH, position: Int) = setData(list[position], position, holder)

    override fun getItemCount() = list.size
}

/**
 * 普通的,内存占用稍高(多new n个fragment和hashmap),但是写着方便
 */
abstract class BaseAdapterOneType2<T>(val list: MutableList<T>,
                                      @LayoutRes private val itemLayoutId: Int)
    : RecyclerView.Adapter<BaseLtViewHolder>() {
    abstract fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder)

    override fun onBindViewHolder(holder: BaseLtViewHolder, position: Int) = setData(holder.viewFind, list[position], position, holder)

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseLtViewHolder(initView(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)))

    open fun initView(itemView: View): View = itemView//对刚生成的view做一些初始化操作
}

/**
 * LtAdapter
 */
abstract class BaseLtAdapterOneType<T, VH : RecyclerView.ViewHolder>(val list: MutableList<T>,
                                                                     val view: View? = LtRecyclerViewManager.getDefualtBottomRefreshView())
    : LtAdapter<VH>(view) {
    /**
     * 给view设置数据
     */
    abstract fun setData(b: T, i: Int, h: VH)

    override fun onLtBindViewHolder(holder: VH, position: Int) = setData(list[position], position, holder)

    override fun getLtItemCount() = list.size
}

/**
 * LtAdapter,内存占用稍高(多new n个fragment和hashmap),但是写着方便
 */
abstract class BaseLtAdapterOneType2<T>(val list: MutableList<T>,
                                        @LayoutRes private val itemLayoutId: Int,
                                        val view: View? = LtRecyclerViewManager.getDefualtBottomRefreshView())
    : LtAdapter<BaseLtViewHolder>(view) {

    abstract fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder)

    override fun onLtBindViewHolder(holder: BaseLtViewHolder, position: Int) = setData(holder.viewFind, list[position], position, holder)

    override fun getLtItemCount() = list.size

    override fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseLtViewHolder(initView(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)))

    open fun initView(itemView: View): View = itemView//对刚生成的view做一些初始化操作
}

/**
 * 使用方便的ViewHolder
 */
class BaseLtViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    val viewFind: ViewFind = ViewFind().apply { this.setView(this@BaseLtViewHolder.view) }
}

/**
 * 使用kt的框架来快捷查找view,并且带有缓存
 */
class ViewFind : Fragment() {
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
                         crossinline setData: BaseAdapterOneType2<T>.(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) -> Unit)
        : BaseAdapterOneType2<T> =
        object : BaseAdapterOneType2<T>(list, itemLayoutId) {
            override fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) = setData(this, v, b, i, h)
        }

/**
 * 快速创建ltAdapter
 */
inline fun <T> ltAdapterOf(list: MutableList<T>,
                           @LayoutRes itemLayoutId: Int,
                           view: View? = LtRecyclerViewManager.getDefualtBottomRefreshView(),
                           crossinline setData: BaseLtAdapterOneType2<T>.(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) -> Unit)
        : BaseLtAdapterOneType2<T> =
        object : BaseLtAdapterOneType2<T>(list, itemLayoutId, view) {
            override fun setData(v: ViewFind, b: T, i: Int, h: BaseLtViewHolder) = setData(this, v, b, i, h)
        }