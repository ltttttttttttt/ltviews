package com.lt.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lt.ltviewsx.lt_recyclerview.LtAdapter

/**
 * 创    建:  lt  2018/7/26--18:21
 * 作    用:
 * 注意事项:
 */

/**
 *  rv的适配器中设置点击事件
 *  作用:减少不必要的创建和销毁click回调对象
 */
inline fun RecyclerView.Adapter<*>.setViewClick(view: View, holder: RecyclerView.ViewHolder, crossinline click: (position: Int) -> Unit) {
    if (view.hasOnClickListeners())
        return
    view.setOnClickListener {
        click(when (this@setViewClick) {
            is LtAdapter<*> -> holder.adapterPosition - (this.headList?.size ?: 0)
            else -> holder.adapterPosition
        })
    }
}