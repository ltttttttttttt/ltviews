package com.lt.view

import android.content.Context
import com.lt.ltviewsx.lt_recyclerview.BaseLtAdapterOneType
import com.lt.ltviewsx.lt_recyclerview.BaseLtViewHolder
import com.lt.ltviewsx.lt_recyclerview.LtRecyclerViewManager
import com.lt.ltviewsx.lt_recyclerview.ViewFind
import kotlinx.android.synthetic.main.layout_test1.*

/**
 * 创    建:  lt  2018/7/11--14:50
 * 作    用:
 * 注意事项:
 */
class TextAdapter(context: Context, list: ArrayList<String>) : BaseLtAdapterOneType<String>(list, R.layout.layout_test1, LtRecyclerViewManager.getDefualtBottomRefreshView()) {
    override fun setData(v: ViewFind, b: String, i: Int, h: BaseLtViewHolder) {
        v.tvLeft.text = b
//        v.setBackgroundResource(R.color.notification_icon_bg_color)
        v.tvRight.text = "右边的$b"
        setViewClick(h.itemView, h) {
            LogUtil.i("lllttt", "TextAdapter.setData : $it")
            v.tvLeft.text = "更改之后的$it"
        }
    }

}