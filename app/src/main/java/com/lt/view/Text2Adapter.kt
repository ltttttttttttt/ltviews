package com.lt.view

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.layout_test1.view.*

/**
 * 创    建:  lt  2018/7/11--14:50
 * 作    用:
 * 注意事项:
 */
class Text2Adapter(context: Context, list: ArrayList<String>?) : BaseAdapterOneType<String>(list, R.layout.layout_test1) {
    override fun setData(v: View, b: String, i: Int) {
        v.tvLeft.text = b
        v.tvRight.text = "右边的$b"
    }
}