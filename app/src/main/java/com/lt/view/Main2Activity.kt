package com.lt.view

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lt.ltviews.lt_recyclerview.LtDivider
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.layout_test2.view.*

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

        val adapter=MAdapter(list)
        val manager = GridLayoutManager(this,3)
        manager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = manager
        rv.adapter = adapter

        rv.addItemDecoration(LtDivider(rv,101,resources.getColor(R.color.colorAccent)))

    }

    class MAdapter(list: ArrayList<String>) : BaseAdapterOneType<String>(list, R.layout.layout_test2) {
        override fun setData(v: View, b: String, i: Int, h: BaseLtViewHolder) {
            v.tvLeft.text = b
            v.tvRight.text = b
            v.setBackgroundResource(R.color.colorPrimary)
        }

    }

}
