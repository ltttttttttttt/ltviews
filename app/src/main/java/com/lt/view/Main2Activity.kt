package com.lt.view

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lt.ltviewsx.lt_recyclerview.*
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
        val manager = GridLayoutManager(this, 3)
        manager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = manager
        rv.adapter = adapter

        rv.addItemDecoration(LtDivider(rv, 101, resources.getColor(R.color.colorAccent)))

    }

    class MAdapter(list: ArrayList<String>) :
        BaseAdapterOneType2<String>(list, R.layout.layout_test2) {
        override fun setData(h: BaseLtViewHolder2, b: String, i: Int) {
            h.tvLeft.text = b
            h.tvRight.text = b
            h.itemView.setBackgroundResource(R.color.colorPrimary)
        }

    }

}
