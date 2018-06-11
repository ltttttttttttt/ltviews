package com.lt.view;

import android.app.Application;

import com.lt.ltviews.lt_recyclerview.LtRecyclerViewManager;
import com.lt.ltviews.lt_recyclerview.MTextRefreshLayout;

/**
 * 创    建:  lt  2018/6/4--14:29
 * 作    用:
 * 注意事项:
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LtRecyclerViewManager.create().init(this)
//                .setRvIsMove(false)
//                .setNoDataIsLoad(true)
                .setRefreshLayoutClazz(MTextRefreshLayout.class);
    }
}
