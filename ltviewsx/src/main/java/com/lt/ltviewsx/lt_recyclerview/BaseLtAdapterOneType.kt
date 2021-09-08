package com.lt.ltviewsx.lt_recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.android.extensions.LayoutContainer

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
 * @param viewBindingClass item布局的viewBinding的class
 */
abstract class BaseAdapterOneType<T, VB : ViewBinding>(
    val list: MutableList<T>,
    private val viewBindingClass: Class<VB>
) : RecyclerView.Adapter<BaseLtViewHolder<VB>>() {
    abstract fun setData(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB)

    override fun onBindViewHolder(holder: BaseLtViewHolder<VB>, position: Int) =
        try {
            setData(holder, list[position], position, holder.binding)
        } catch (t: Throwable) {
            //如果没有对异常做正确处理,可能会显示异常
            LtRecyclerViewManager.onLtViewsCatchHandler(t)
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder<VB> =
        BaseLtViewHolder(viewBindingClass.createViewBinding(LayoutInflater.from(parent.context)))
}

/**
 * LtAdapter的封装适配器
 *
 * @param T bean类的泛型
 * @param list 数据list
 */
abstract class BaseLtAdapterOneType<T, VB : ViewBinding>(
    val list: MutableList<T>,
    private val viewBindingClass: Class<VB>,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView()
) : LtAdapter<BaseLtViewHolder<VB>>(view) {

    abstract fun setData(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB)

    override fun onLtBindViewHolder(holder: BaseLtViewHolder<VB>, position: Int) =
        setData(holder, list[position], position, holder.binding)

    override fun getLtItemCount() = list.size

    override fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLtViewHolder<VB> =
        BaseLtViewHolder(viewBindingClass.createViewBinding(LayoutInflater.from(parent.context)))
}

/**
 * 使用方便的ViewHolder
 * [LayoutContainer]使用kt的框架来快捷查找view,并且带有缓存
 */
open class BaseLtViewHolder<VB : ViewBinding>(val binding: VB) :
    RecyclerView.ViewHolder(binding.root)

/**
 * 快速创建adapter
 */
inline fun <T, reified VB : ViewBinding> adapterOf(
    list: MutableList<T>,
    crossinline setData: BaseAdapterOneType<T, VB>.(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB) -> Unit
): BaseAdapterOneType<T, VB> =
    object : BaseAdapterOneType<T, VB>(list, VB::class.java) {
        override fun setData(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB) {
            setData(this, h, b, i, v)
        }
    }

/**
 * 快速创建ltAdapter
 */
inline fun <T, reified VB : ViewBinding> ltAdapterOf(
    list: MutableList<T>,
    view: View? = LtRecyclerViewManager.getDefaultBottomRefreshView(),
    crossinline setData: BaseLtAdapterOneType<T, VB>.(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB) -> Unit
): BaseLtAdapterOneType<T, VB> =
    object : BaseLtAdapterOneType<T, VB>(list, VB::class.java, view) {
        override fun setData(h: BaseLtViewHolder<VB>, b: T, i: Int, v: VB) {
            setData(this, h, b, i, v)
        }
    }

/**
 * 通过viewBinding的Class来获取binding对象
 */
private fun <VB : ViewBinding> Class<VB>.createViewBinding(layoutInflater: LayoutInflater): VB =
    getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, layoutInflater) as VB