package com.lt.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lt.ltviewsx.lt_listener.OnUpAndDownListener
import com.lt.ltviewsx.lt_recyclerview.LTRecyclerView
import com.lt.ltviewsx.lt_recyclerview.adapterOf
import kotlinx.android.synthetic.main.a_main3.*

/**
 * creator: lt  2020/8/22  lt.dygzs@qq.com
 * effect :
 * warning:
 */
class Main3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main3)
        val colors = intArrayOf(R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5).map(resources::getColor)
        vp.adapter = adapterOf(mutableListOf(1, 2, 3, 4, 5), R.layout.item_ltrecy) { v, b, i, h ->
            val ltRecyclerView = h.itemView as LTRecyclerView
            ltRecyclerView.setOnUpAndDownListener(object : OnUpAndDownListener {
                override fun up() {
                }

                override fun down() {
                    ltRecyclerView.postDelayed({ ltRecyclerView.setTopRefresh(false) }, 2000)
                }
            })
            ltRecyclerView.adapter = TextAdapter(ltRecyclerView.context, ArrayList((0..50).map(Int::toString)))
            ltRecyclerView.recyclerView.setBackgroundColor(colors[i])
        }
    }
}