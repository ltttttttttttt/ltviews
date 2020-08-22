package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lt.ltviewsx.R
import com.lt.ltviewsx.lt_listener.OnNoItemListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener
import com.lt.ltviewsx.lt_listener.OnRvItemLongClickListener
import com.lt.ltviewsx.utils.nullSize
import com.lt.ltviewsx.utils.yesOrNo
import java.util.*
/**
 * creator: lt  2017/4/28   lt.dygzs@qq.com
 * effect : 适配器
 * warning: 使用方法: https://blog.csdn.net/qq_33505109/article/details/80653174
 *                   https://blog.csdn.net/qq_33505109/article/details/80677331
 *                   https://blog.csdn.net/qq_33505109/article/details/80677778
 *           github: https://github.com/ltttttttttttt/ltviews
 */
abstract class LtAdapter<VH : RecyclerView.ViewHolder> @JvmOverloads constructor(bottomRefreshView: View? = LtRecyclerViewManager.getDefaultBottomRefreshView())
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
     * 头部的条目集合,只供预览,请调用xxHeadView(),如果直接修改headList请设置headIsChanged为true,否则不生效
     */
    var headList = LinkedList<View>()

    /**
     * 尾部的条目集合,只供预览,请调用xxTailView(),如果直接修改tailList请设置tailIsChanged为true,否则不生效
     */
    var tailList = LinkedList<View>()

    /**
     * 头布局算不算在条目内(用于noItem算法)
     */
    var headsIsItem = true

    /**
     * 头布局算不算在条目内(用于noItem算法)
     */
    var tailsIsItem = true

    /**
     * 头部是否变动
     */
    var headIsChanged = false

    /**
     * 尾部是否变动
     */
    var tailIsChanged = false

    private var onNoItemListenerList: MutableList<OnNoItemListener>? = null//有无条目的回调
    private var onRvItemClickListener: OnRvItemClickListener? = null//条目点击事件
    private var onRvItemLongClickListener: OnRvItemLongClickListener? = null//条目长按事件
    private var noItemListenerState: Boolean? = null//没有条目回调的状态:null第一次  true上次是有数据  false上次是无数据
    private var bottomRefreshState = if (bottomRefreshView == null) -1 else 0//底部刷新的状态 -1表示不能刷新,其他表示展示的索引

    companion object {
        private const val TAG_BOTTOM_REFRESH_VIEW = 12345701//底部刷新view
        private const val TAG_HEAD_VIEW = 12345702//头部view
        private const val TAG_TAIL_VIEW = 12345703//尾部view
        private val TAG_IS_HAVE_LONG_CLICK = R.id.iv_lt_refresh//是否包含长按事件,ps:必须要用资源id
    }

    //底部刷新
    private class BottomRefreshViewHolder(view: View) : RecyclerView.ViewHolder(view)

    //头部
    private class HeadViewHolder(context: Context) : RecyclerView.ViewHolder(LinearLayout(context).apply {
        layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        orientation = LinearLayout.VERTICAL
    }) {
        val linearLayout = itemView as LinearLayout
    }

    //尾部
    private class TailViewHolder(context: Context) : RecyclerView.ViewHolder(LinearLayout(context).apply {
        layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        orientation = LinearLayout.VERTICAL
    }) {
        val linearLayout = itemView as LinearLayout
    }

    /**
     * 使用空参构造会使用默认的上拉View,传入null则不使用底部刷新布局
     */
    init {
        this.bottomRefreshView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        (this.bottomRefreshView as? ViewGroup)?.let {
            repeat(it.childCount) { index ->
                bottomRefreshViewMap.put(index, it.getChildAt(index))
            }
        }
    }

    /**
     * 创建ViewHolder
     */
    abstract fun onLtCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * 返回显示的条目的数量
     */
    abstract fun getLtItemCount(): Int

    /**
     * 用于设置数据
     */
    abstract fun onLtBindViewHolder(holder: VH, position: Int)

    /**
     * 获取Type
     */
    open fun getLtItemViewType(position: Int): Int = 0

    /**
     * 设置是否是刷新,没有更多数据的时候传入false,否则true
     */
    fun setRefresh(b: Boolean): LtAdapter<VH> {
        return setRefresh(b.yesOrNo(0, 1))
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
     * 设置条目的点击事件监听,请注意不要给holder.itemView设置点击事件
     */
    fun setOnRvItemClickListener(onRvItemClickListener: OnRvItemClickListener?): LtAdapter<VH> {
        this.onRvItemClickListener = onRvItemClickListener
        return this
    }

    /**
     * 设置条目的长按事件监听,请注意不要给holder.itemView设置长按事件
     */
    fun setOnRvItemLongClickListener(onRvItemLongClickListener: OnRvItemLongClickListener?): LtAdapter<VH> {
        this.onRvItemLongClickListener = onRvItemLongClickListener
        return this
    }

    /**
     * 获取是否上拉没数据状态,false表示没更多数据,true表示上拉还能获取到数据
     * ps:表示是否还能在继续上拉获取到数据
     */
    val refreshViewIsHaveData: Boolean
        get() = noDataIsLoad || bottomRefreshViewMap[1]?.visibility != View.VISIBLE//如果ll2为null或已经无数据,就可以返回是否要加载数据

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
        headIsChanged = true
        headList.add(view)
        return this
    }

    fun addHeadView(view: View, position: Int): LtAdapter<VH> {
        headIsChanged = true
        headList.add(position, view)
        return this
    }

    /**
     * 移除头布局
     */
    fun removeHeadView(view: View): LtAdapter<VH> {
        headIsChanged = true
        headList.remove(view)
        return this
    }

    fun removeHeadView(position: Int): LtAdapter<VH> {
        headIsChanged = true
        headList.removeAt(position)
        return this
    }

    /**
     * 移除全部头布局
     */
    fun clearHeadView(): LtAdapter<VH> {
        headIsChanged = true
        headList.clear()
        return this
    }

    /**
     * 添加底部的布局
     */
    fun addTailView(view: View): LtAdapter<VH> {
        tailIsChanged = true
        tailList.add(view)
        return this
    }

    fun addTailView(view: View, position: Int): LtAdapter<VH> {
        tailIsChanged = true
        tailList.add(position, view)
        return this
    }

    /**
     * 移除尾布局
     */
    fun removeTailView(view: View): LtAdapter<VH> {
        tailIsChanged = true
        tailList.remove(view)
        return this
    }

    fun removeTailView(position: Int): LtAdapter<VH> {
        tailIsChanged = true
        tailList.removeAt(position)
        return this
    }

    /**
     * 移除全部尾布局
     */
    fun clearTailView(): LtAdapter<VH> {
        tailIsChanged = true
        tailList.clear()
        return this
    }

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtCreateViewHolder}")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { //顶部和底部返回特定的ViewHolder
        return when (viewType) {
            TAG_BOTTOM_REFRESH_VIEW -> BottomRefreshViewHolder(bottomRefreshView) //底部刷新布局
            TAG_HEAD_VIEW -> HeadViewHolder(parent.context)//头布局
            TAG_TAIL_VIEW -> TailViewHolder(parent.context)//尾布局
            else -> onLtCreateViewHolder(parent, viewType)
        }
    }

    //给顶部和底部的布局加上特定的type
    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemViewType}")
    override fun getItemViewType(position: Int): Int {
        val itemCount = itemCount
        return when {
            position == 0 && headList.isNotEmpty() -> TAG_HEAD_VIEW //表示头部
            position == itemCount - 2 && tailList.isNotEmpty() -> TAG_TAIL_VIEW //表示尾部
            position == itemCount - 1 -> TAG_BOTTOM_REFRESH_VIEW //表示是底部的上拉加载布局
            else -> getLtItemViewType(position - headList.isNotEmpty().yesOrNo(1, 0))//中间自定的布局
        }
    }

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemCount}")
    override fun getItemCount(): Int {
        val itemCount = getLtItemCount()
        //如果调用了一次无数据,下次有数据的时候就调用有数据,如果之前没调过无数据,就不相应有数据
        if (noItemListenerState != false
                && itemCount == 0
                && onNoItemListenerList.nullSize() > 0
                && (!headsIsItem || headList.isEmpty())
                && (!tailsIsItem || tailList.isEmpty())) {
            //如果没数据,但是变成有数据了,就调用有数据的回调,并修改为有数据
            noItemListenerState = false
            onNoItemListenerList?.forEach { it.noItem() }
        } else if (noItemListenerState != true
                && onNoItemListenerList.nullSize() > 0
                && ((headsIsItem && headList.isNotEmpty())
                        || (tailsIsItem && tailList.isNotEmpty())
                        || itemCount > 0)) {
            noItemListenerState = true
            onNoItemListenerList?.forEach { it.haveItem() }
        }
        //加上多的顶部和底部的条目
        var count = itemCount + 1
        if (headList.isNotEmpty())
            count++
        if (tailList.isNotEmpty())
            count++
        return count
    }

    @Deprecated("一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtBindViewHolder}")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //给不是头部,不是尾部,不是上拉的布局提供方法,并减去头部的条目数
        //如果是头部或尾部,如果变更过,就刷新viewHolder
        if (holder is HeadViewHolder) {
            if (headIsChanged) {
                holder.linearLayout.removeAllViews()
                headList.forEach {
                    holder.linearLayout.addView(it)
                }
                headIsChanged = false
            }
            return
        }
        if (holder is BottomRefreshViewHolder)
            return
        if (holder is TailViewHolder) {
            if (tailIsChanged) {
                holder.linearLayout.removeAllViews()
                tailList.forEach {
                    holder.linearLayout.addView(it)
                }
                tailIsChanged = false
            }
            return
        }
        //条目长按事件,TAG_IS_HAVE_LONGCLICK:表示view是否设置过长按事件
        if (onRvItemLongClickListener != null && holder.itemView.getTag(TAG_IS_HAVE_LONG_CLICK) != true) {
            holder.itemView.setTag(TAG_IS_HAVE_LONG_CLICK, true)
            holder.itemView.setOnLongClickListener {
                onRvItemLongClickListener ?: return@setOnLongClickListener false
                val mPosition = holder.adapterPosition - headList.isEmpty().yesOrNo(0, 1)
                if (mPosition >= 0)
                    onRvItemLongClickListener?.onItemLongClick(it, mPosition)
                true
            }
        }
        //条目点击事件
        if (onRvItemClickListener != null && !holder.itemView.hasOnClickListeners())
            holder.itemView.setOnClickListener {
                onRvItemClickListener ?: return@setOnClickListener
                val mPosition = holder.adapterPosition - headList.isEmpty().yesOrNo(0, 1)
                if (mPosition >= 0)
                    onRvItemClickListener?.onItemClick(it, mPosition)
            }
        onLtBindViewHolder(holder as VH, position - headList.isEmpty().yesOrNo(0, 1))
    }

    @Deprecated("设置没有数据时的回调, 调用后, 则不会自动显示和隐藏没条目的view,并且set null 会清空掉数据,请使用{@link LtAdapter#addOnNoItemListener}")
    fun setOnNoItemListener(onNoItemListener: OnNoItemListener?): LtAdapter<VH> {
        if (onNoItemListenerList == null) onNoItemListenerList = ArrayList() else onNoItemListenerList!!.clear()
        if (onNoItemListener != null)
            onNoItemListenerList!!.add(onNoItemListener)
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
                return if (itemViewType == TAG_BOTTOM_REFRESH_VIEW
                        || itemViewType == TAG_HEAD_VIEW
                        || itemViewType == TAG_TAIL_VIEW) gridManager.spanCount
                else 1
            }
        }
    }
}