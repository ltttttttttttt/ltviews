package com.lt.view

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lt.ltviewsx.lt_listener.OnUpAndDownListener
import com.lt.ltviewsx.lt_recyclerview.BaseLtAdapterOneType2
import com.lt.ltviewsx.lt_recyclerview.BaseLtViewHolder2
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.layout_test2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        rl.setOnRefreshListener {
            Handler().postDelayed({
                kotlin.run {
                    rl.isRefreshing = false
                }
            }, 2000)
        }

        val list = ArrayList<String>()
        for (i in 0..99) {
            list.add("$i")
        }

        val adapter = MAdapter(list)
        rv.setSpanCount(3)
        rv.adapter = adapter
        val tv = TextView(this)
// ps:如果头布局的第一个条目被gone了,就会导致下拉刷新无效,实际是因为RecyclerView的canScrollVertically始终为true,如果需要修改此问题,可以重写LayoutManager的computeScrollOffset方法,不知道是特性还是bug
//        tv.visibility = View.GONE
        adapter.addHeadView(tv)
        rv.setOnUpAndDownListener(object : OnUpAndDownListener {
            override fun up() {
            }

            override fun down() {
                rv.postDelayed({
                    rv.setTopRefresh(false)
                }, 3000)
            }
        })

    }

    class MAdapter(list: ArrayList<String>) :
        BaseLtAdapterOneType2<String>(list, R.layout.layout_test2) {
        override fun setData(h: BaseLtViewHolder2, b: String, i: Int) {
            h.tvLeft.text = b
            h.tvRight.text = b
            h.itemView.setBackgroundResource(R.color.colorPrimary)
        }

    }

}
