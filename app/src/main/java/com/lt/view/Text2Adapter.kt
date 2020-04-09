package com.lt.view

import android.content.Context
import com.lt.ltviewsx.lt_recyclerview.BaseAdapterOneType
import com.lt.ltviewsx.lt_recyclerview.BaseLtViewHolder
import com.lt.ltviewsx.lt_recyclerview.ViewFind
import kotlinx.android.synthetic.main.layout_test1.*

/**
 * 创    建:  lt  2018/7/11--14:50
 * 作    用:
 * 注意事项:
 */
class Text2Adapter(context: Context, list: ArrayList<String>) : BaseAdapterOneType<String>(list, R.layout.layout_test1) {
    override fun setData(v: ViewFind, b: String, i: Int, h: BaseLtViewHolder) {
        v.tvLeft.text = b
        v.tvRight.text = "右边的$b"
        h.itemView.setOnClickListener {
            LogUtil.i("lllttt", "TextAdapter.setData : $it")
            v.tvLeft.text = "更改之后的$it"
        }
    }
}