package com.lt.view;

import android.app.Application;

import com.lt.ltviewsx.lt_recyclerview.LtRecyclerViewManager;


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
        // TODO by lt 2020/8/24 11:29 内部嵌套sv和nsv有滑动冲突问题
        init.setRvIsMove(false);
//                .setNoDataIsLoad( true)
//        init.setRefreshLayoutConstructorFunction(new Function3<Context, AttributeSet, Integer, BaseRefreshLayout>() {
//            @Override
//            public BaseRefreshLayout invoke(Context context, AttributeSet attributeSet, Integer integer) {
//                return new MTextRefreshLayout(context, attributeSet, integer);
//            }
//        });
        //init.setRefreshLayoutConstructorFunction(MTextRefreshLayout::new);//java8
        //init.setRefreshLayoutConstructorFunction(::MTextRefreshLayout);//kt
    }
}
