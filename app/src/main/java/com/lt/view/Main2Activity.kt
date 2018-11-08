package com.lt.view

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*

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
    }

}
