package com.lt.ltviewsx.lt_recyclerview

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lt.ltviewsx.lt_listener.OnNoItemListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener
import com.lt.ltviewsx.lt_listener.OnRvItemLongClickListener
import com.lt.ltviewsx.utils.nullSize
import java.util.*

/**
 * 所在包名:  com.lt.ltrecyclerview
 * 创建日期:  2017/4/28--10:38
 * 作   用:   适配器
 * 使用方法:
 * 注意事项:
 */
abstract class LtAdapter<VH : RecyclerView.ViewHolder> @JvmOverloads constructor(bottomRefreshView: View? = LtRecyclerViewManager.getDefualtBottomRefreshView())
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * 底部的上拉刷新View
     */
    val bottomRefreshView = bottomRefreshView ?: View(LtRecyclerViewManager.context)
    /**
     * 没数据时是否刷新
     */
    var noDataIsLoad = LtRecyclerViewManager.isNoDataIsLoad
    /**
     * 底部的上拉刷新View集合,并且默认的,布局中的第0个view表示刷新中 1表示刷新完成 其他提供给自定义
     */
    val bottomRefreshViewMap = SparseArray<View>((bottomRefreshView as? ViewGroup)?.childCount ?: 0)
    /**
     * 头部的条目集合
     */
    var headList: MutableList<View>? = null
    /**
     * 尾部的条目集合
     */
    var tailList: MutableList<View>? = null
    /**
     * 头布局算不算在条目内(用于noItem算法)
     */
    var headersIsItem = true
    /**
     * 头布局算不算在条目内(用于noItem算法)
     */
    var tailsIsItem = true
    private var onNoItemListenerList: MutableList<OnNoItemListener>? = null//有无条目的回调
    private var onRvItemClickListener: OnRvItemClickListener? = null//条目点击事件
    private var onRvItemLongClickListener: OnRvItemLongClickListener? = null//条目长按事件
    private var noItemListenerState: Boolean? = null//没有条目回调的状态:null第一次  true上次是有数据  false上次是无数据
    private var bottomRefreshState = if (bottomRefreshView == null) -1 else 0//底部刷新的状态 -1表示不能刷新,其他表示展示的索引

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)//内部方便使用

    /**
     * 使用空参构造会使用默认的上拉View,传入null则不使用底部刷新布局
     */
    init {
        this.bottomRefreshView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val viewGroup = this.bottomRefreshView as? ViewGroup
        repeat(viewGroup?.childCount ?: 0) {
            bottomRefreshViewMap.put(it, viewGroup!!.getChildAt(it))
        }
    }

    /**
     * 创建ViewHolder
     */
    abstract fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtCreateViewHolder}")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { //顶部和底部返回特定的ViewHolder
        return when (viewType) {
            12345701 -> MyViewHolder(bottomRefreshView!!) //底部刷新布局
            in 12345500..12345599 -> MyViewHolder(headList!![viewType - 12345500]) //头布局
            in 12345600..12345699 -> MyViewHolder(tailList!![viewType - 12345600]) //尾布局
            else -> onLtCreateViewHolder(parent, viewType)
        }
    }

    /**
     * 获取Type
     */
    open fun getLtItemViewType(position: Int): Int = 0

    //给顶部和底部的布局加上特定的type
    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemViewType}")
    override fun getItemViewType(position: Int): Int {
        return when {
            headList != null
                    && position <= headList.nullSize() - 1
            -> 12345500 + position //表示头部
            tailList != null
                    && position >= getLtItemCount() + headList.nullSize()
                    && position < itemCount - 1
            -> 12345600 + (position - getLtItemCount() - headList.nullSize()) //表示尾部
            position == itemCount - 1 -> 12345701 //表示是底部的上拉加载布局
            else -> getLtItemViewType(position - headList.nullSize())//中间自定的布局
        }
    }

    /**
     * 返回显示的条目的数量
     */
    abstract fun getLtItemCount(): Int

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemCount}")
    override fun getItemCount(): Int {
        //如果调用了一次无数据,下次有数据的时候就调用有数据,如果之前没调过无数据,就不相应有数据
        if (noItemListenerState != false
                && onNoItemListenerList.nullSize() > 0
                && (!headersIsItem || headList.nullSize() == 0)
                && (!tailsIsItem || tailList.nullSize() == 0)
                && getLtItemCount() == 0) {
            //如果没数据,但是变成有数据了,就调用有数据的回调,并修改为有数据
            noItemListenerState = false
            onNoItemListenerList?.forEach { it.noItem() }
        } else if (noItemListenerState != true
                && onNoItemListenerList.nullSize() > 0
                && ((headersIsItem && headList.nullSize() != 0)
                        || (tailsIsItem && tailList.nullSize() != 0)
                        || getLtItemCount() > 0)) {
            noItemListenerState = true
            onNoItemListenerList?.forEach { it.haveItem() }
        }
        //加上多的顶部和底部的条目
        return getLtItemCount() + 1 + headList.nullSize() + tailList.nullSize()
    }

    /**
     * 设置是否是刷新,没有更多数据的时候传入false,否则true
     */
    fun setRefresh(b: Boolean): LtAdapter<VH> {
        return setRefresh(if (b) 0 else 1)
    }

    /**
     * 设置是否是刷新,没有更多数据的时候传入false,否则true
     * 提供给自定义底部刷新的布局控制
     */
    fun setRefresh(state: Int): LtAdapter<VH> {
        if (bottomRefreshState < 0 || state == bottomRefreshState)
            return this
        bottomRefreshViewMap[bottomRefreshState].visibility = View.GONE
        bottomRefreshViewMap[state].visibility = View.VISIBLE
        bottomRefreshState = state
        return this
    }

    /**
     * 用于设置数据
     */
    abstract fun onLtBindViewHolder(holder: VH, position: Int)

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtBindViewHolder}")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //给不是头部,不是尾部,不是上拉的布局提供方法,并减去头部的条目数
        if (position < headList.nullSize())
            return
        if (position >= getLtItemCount() + headList.nullSize())
            return
        onLtBindViewHolder(holder as VH, position - headList.nullSize())
        //条目长按事件
        if (onRvItemLongClickListener != null && holder.itemView.getTag(12345678) != true) {
            holder.itemView.setTag(12345678, true)
            holder.itemView.setOnLongClickListener {
                onRvItemLongClickListener ?: return@setOnLongClickListener false
                onRvItemLongClickListener?.onItemLongClick(it, holder.adapterPosition)
                true
            }
        }
        //条目点击事件
        if (onRvItemClickListener != null && !holder.itemView.hasOnClickListeners())
            holder.itemView.setOnClickListener {
                onRvItemClickListener ?: return@setOnClickListener
                onRvItemClickListener?.onItemClick(it, holder.adapterPosition)
            }
    }

    @Deprecated("设置没有数据时的回调, 调用后, 则不会自动显示和隐藏没条目的view,并且set null 会清空掉数据,请使用{@link LtAdapter#addOnNoItemListener}")
    fun setOnNoItemListener(onNoItemListener: OnNoItemListener?): LtAdapter<VH> {
        if (onNoItemListenerList == null) onNoItemListenerList = ArrayList() else onNoItemListenerList!!.clear()
        if (onNoItemListener != null)
            onNoItemListenerList!!.add(onNoItemListener)
        return this
    }

    /**
     * 添加没有数据时的回调
     */
    fun addOnNoItemListener(onNoItemListener: OnNoItemListener): LtAdapter<VH> {
        if (onNoItemListenerList == null) onNoItemListenerList = ArrayList()
        onNoItemListenerList!!.add(onNoItemListener)
        return this
    }

    fun addOnNoItemListener(position: Int, onNoItemListener: OnNoItemListener): LtAdapter<VH> {
        if (onNoItemListenerList == null) onNoItemListenerList = ArrayList()
        onNoItemListenerList!!.add(position, onNoItemListener)
        return this
    }

    /**
     * 移除没有数据时的回调
     */
    fun removeOnNoItemListener(onNoItemListener: OnNoItemListener): LtAdapter<VH> {
        if (onNoItemListenerList.nullSize() == 0) return this
        onNoItemListenerList!!.remove(onNoItemListener)
        return this
    }

    /**
     * 获取没有数据时的回调
     */
    fun getOnNoItemListenerList(): List<OnNoItemListener>? = onNoItemListenerList

    /**
     * 添加头部的布局
     */
    fun addHeadView(view: View): LtAdapter<VH> {
        if (headList == null) headList = ArrayList()
        headList!!.add(view)
        return this
    }

    fun addHeadView(view: View, position: Int): LtAdapter<VH> {
        if (headList == null) headList = ArrayList()
        headList!!.add(position, view)
        return this
    }

    /**
     * 添加底部的布局
     */
    fun addTailView(view: View): LtAdapter<VH> {
        if (tailList == null) tailList = ArrayList()
        tailList!!.add(view)
        return this
    }

    fun addTailView(view: View, position: Int): LtAdapter<VH> {
        if (tailList == null) tailList = ArrayList()
        tailList!!.add(position, view)
        return this
    }

    /**
     * 适用于GridView,使条目跨列
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val gridManager = recyclerView.layoutManager as? GridLayoutManager ?: return
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemViewType = getItemViewType(position)
                return if (itemViewType == 12345701
                        || itemViewType in 12345500..12345599
                        || itemViewType in 12345600..12345699) gridManager.spanCount
                else 1
            }
        }
    }

    /**
     * 设置条目的点击事件监听,请注意不要给holder.itemView设置点击事件,每setAdapter一次只能设置一次,不然会有bug
     */
    fun setOnRvItemClickListener(onRvItemClickListener: OnRvItemClickListener?): LtAdapter<VH> {
        this.onRvItemClickListener = onRvItemClickListener
        notifyDataSetChanged()
        return this
    }

    /**
     * 设置条目的长按事件监听,请注意不要给holder.itemView设置长按事件,每setAdapter一次只能设置一次,不然会有bug
     */
    fun setOnRvItemLongClickListener(onRvItemLongClickListener: OnRvItemLongClickListener?): LtAdapter<VH> {
        this.onRvItemLongClickListener = onRvItemLongClickListener
        notifyDataSetChanged()
        return this
    }

    /**
     * 获取是否上拉没数据状态,false表示没更多数据,true表示上拉还能获取到数据
     * ps:表示是否还能在继续上拉获取到数据
     */
    val refreshViewIsHaveData: Boolean
        get() = noDataIsLoad || bottomRefreshViewMap[1]?.visibility != View.VISIBLE//如果ll2为null或已经无数据,就可以返回是否要加载数据
}