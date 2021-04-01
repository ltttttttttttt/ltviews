package com.lt.ltviewsx.lt_recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * creator: lt  2020/9/19  lt.dygzs@qq.com
 * effect : 解决某些rv内部bug的GridLayoutManager
 * warning:
 */
class MGridLayoutManager : GridLayoutManager {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)


    constructor(context: Context, spanCount: Int) :
            super(context, spanCount)


    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) :
            super(context, spanCount, orientation, reverseLayout)

    /**
     * 解决某些rv内部bug(数组越界,等?)
     * 参考https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
     */
    override fun supportsPredictiveItemAnimations(): Boolean = false

    /**
     * 解决rv内部ViewHolder越界Adapter总ItemCount异常
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (t: Throwable) {
            LtRecyclerViewManager.onAdapterCatchHandler(t)
        }
    }
}