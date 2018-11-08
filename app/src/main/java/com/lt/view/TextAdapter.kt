package com.lt.view

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import kotlinx.android.synthetic.main.layout_test1.view.*

/**
 * 创    建:  lt  2018/7/11--14:50
 * 作    用:
 * 注意事项:
 */
class TextAdapter(context: Context, list: ArrayList<String>?) : BaseLtAdapterOneType<String>(null, list, R.layout.layout_test1) {
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    override fun setData(v: View, b: String, i: Int, h: BaseLtViewHolder) {
        v.tvLeft.text = b
        v.setBackgroundResource(R.color.notification_icon_bg_color)
        v.tvRight.text = "右边的$b"
        setViewClick(v, h) {
            LogUtil.i("lllttt", "TextAdapter.setData : $it")
            v.tvLeft.text = "更改之后的$it"
        }
    }
}