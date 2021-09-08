package com.lt.view

import android.content.Context
import com.lt.ltviewsx.lt_recyclerview.*
import kotlinx.android.synthetic.main.layout_test1.*

/**
 * 创    建:  lt  2018/7/11--14:50
 * 作    用:
 * 注意事项:
 */
class TextAdapter(context: Context, list: ArrayList<String>) : BaseLtAdapterOneType2<String>(
    list,
    R.layout.layout_test1,
    LtRecyclerViewManager.getDefaultBottomRefreshView()
) {
    override fun setData(h: BaseLtViewHolder2, b: String, i: Int) {
        h.tvLeft.text = b
//        h.setBackgroundResource(R.color.notification_icon_bg_color)
        h.tvRight.text = "右边的$b"
        setViewClick(h.itemView, h) {
            LogUtil.i("lllttt", "TextAdapter.setData : $it")
            h.tvLeft.text = "更改之后的$it"
        }
    }

}