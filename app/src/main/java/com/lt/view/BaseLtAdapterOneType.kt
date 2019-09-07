package com.lt.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lt.ltviews.lt_recyclerview.LtAdapter

/**
 * 创    建:  lt  2018/3/9--11:22
 * 作    用:  LtAdapter 的Base
 * 注意事项:  只能在有一种type的情况下使用
 */

/*  示例代码
class CountryAdapter(context: Context, list: ArrayList<ClassBean>?) : BaseLtAdapterOneType<ClassBean>(null, list, 0) {
        override fun setData(v: View, b: ClassBean, i: Int) {
        }
    }
 */

//LtAdapter
abstract class BaseLtAdapterOneType<T>(var view: View?, var list: ArrayList<T>?, var itemLayoutId: Int) : LtAdapter<BaseLtViewHolder>(view) {

    abstract fun setData(v: View, b: T, i: Int, h: BaseLtViewHolder)

    override fun onLtBindViewHolder(p0: BaseLtViewHolder, p1: Int) = setData(p0.itemView, list!![p1], p1, p0)

    override fun getLtItemCount() = list?.size ?: 0

    override fun onLtCreateViewHolder(p0: ViewGroup, p1: Int) = BaseLtViewHolder(LayoutInflater.from(p0.context).inflate(itemLayoutId, p0, false))
}

//普通的
abstract class BaseAdapterOneType<T>(var list: ArrayList<T>?, var itemLayoutId: Int) : RecyclerView.Adapter<BaseLtViewHolder>() {
    abstract fun setData(v: View, b: T, i: Int, h: BaseLtViewHolder)

    override fun onBindViewHolder(holder: BaseLtViewHolder?, position: Int) = setData(holder!!.itemView, list!![position], position, holder)

    override fun getItemCount() = list?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = BaseLtViewHolder(LayoutInflater.from(parent?.context).inflate(itemLayoutId, parent, false))
}

class BaseLtViewHolder(view: View) : RecyclerView.ViewHolder(view)