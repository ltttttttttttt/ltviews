package com.lt.view;

import android.app.Application;

import com.lt.ltviewsx.lt_recyclerview.LtRecyclerViewManager;
import com.lt.ltviewsx.lt_recyclerview.MTextRefreshLayout;


/**
 * 创    建:  lt  2018/6/4--14:29
 * 作    用:
 * 注意事项:
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LtRecyclerViewManager init = LtRecyclerViewManager.INSTANCE.init(this);
        init.setUpLayoutId(R.layout.lt_up_loading);
//                .setRvIsMove(false)
//                .setNoDataIsLoad( true)
        init.setRefreshLayoutClazz(MTextRefreshLayout.class);
    }
}
