package com.lt.ltviewsx.lt_recyclerview

import android.R
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lt.ltviewsx.utils.nullSize

/**
 * rv的分割线类
 */
class LtDivider(rv: RecyclerView) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable?
    private var mDividerHeight = 2 //分割线高度，默认为2px
    private val orientation: Int//方向,-1为gridview
    private var spanCount = 0

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param rv
     */
    init {
        val a = rv.context.obtainStyledAttributes(intArrayOf(R.attr.listDivider))
        mDivider = a.getDrawable(0)
        a.recycle()
        orientation = if (rv.layoutManager is LinearLayoutManager) (rv.layoutManager as LinearLayoutManager?)!!.orientation else -1
    }

    /**
     * 自定义分割线
     *
     * @param rv
     * @param drawableId 分割线图片
     */
    constructor(rv: RecyclerView, drawableId: Int) : this(rv, ContextCompat.getDrawable(rv.context, drawableId)!!)

    constructor(rv: RecyclerView, drawable: Drawable) : this(rv) {
        mDivider = drawable
        mDividerHeight = drawable.intrinsicHeight
    }

    /**
     * 自定义分割线
     *
     * @param rv
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    constructor(rv: RecyclerView, dividerHeight: Int, dividerColor: Int) : this(rv) {
        mDividerHeight = dividerHeight
        mDivider = GradientDrawable()
        (mDivider as GradientDrawable).setColor(dividerColor)
    }

    //获取分割线尺寸
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount
        when {
            parent.layoutManager!!.getPosition(view) == childCount - 1 // 如果是最后一行，则不需要绘制底部
            -> {
                outRect[0, 0, 0] = 0
            }
            isLastColum(parent, spanCount, view) // 如果是最后一列，则不需要绘制右边
            -> {
                outRect[0, 0, 0] = mDividerHeight
            }
            else -> {
                outRect[0, 0, mDividerHeight] = mDividerHeight
            }
        }
    }

    private fun getSpanCount(parent: RecyclerView): Int { // 列数
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    private fun isLastColum(parent: RecyclerView, spanCount: Int, view: View): Boolean {
        val layoutManager = parent.layoutManager
        val pos = layoutManager!!.getPosition(view)
        if (layoutManager is GridLayoutManager) { // 如果是有head,则去掉head,如果是最后一列，则不需要绘制右边
            if (parent.adapter is LtAdapter<*>) {
                val headSize = (parent.adapter as LtAdapter<*>?)!!.headList.nullSize()
                if ((pos - headSize + 1) % spanCount == 0) {
                    return true
                }
            } else if ((pos + 1) % spanCount == 0) { // 如果是最后一列，则不需要绘制右边
                return true
            }
        }
        return false
    }

    //绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        when {
            spanCount > 1 -> {
                drawVertical(c, parent)
                drawHorizontal(c, parent)
            }
            orientation == LinearLayoutManager.VERTICAL -> {
                drawHorizontal(c, parent)
            }
            orientation == LinearLayoutManager.HORIZONTAL -> {
                drawVertical(c, parent)
            }
        }
    }

    //绘制横向 item_person_select 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = (child.right + params.rightMargin
                    +  /*mDivider.getIntrinsicWidth()*/mDividerHeight)
            val top = child.bottom + params.bottomMargin
            val bottom = top +  /*mDivider.getIntrinsicHeight()*/mDividerHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
    }

    //绘制纵向 item_person_select 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        var childCount = parent.childCount
        if (parent.adapter is LtAdapter<*>) childCount--
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            val right = left +  /*mDivider.getIntrinsicWidth()*/mDividerHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
    }
}