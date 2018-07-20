package com.lt.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val llm = LinearLayoutManager(this);
        rv.layoutManager = llm
        val list = ArrayList<String>()
        for (i in 0..99998) {
            list.add("" + i)
        }
        rv.adapter = Text2Adapter(this, list)
    }
}
