package com.lt.ltviews.lt_recyclerview;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

/**
 * 创    建:  lt  2018/5/23--17:02
 * 作    用:  刷新的布局
 * 注意事项:
 */

public interface BaseRefreshLayout {
    void setEnabled(boolean enabled);//设置下拉布局是否可用

    void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener);//设置刷新的监听

    void setRefreshing(boolean refreshing);//设置是否刷新

    boolean isRefreshing();//是否正在刷新

    void setLayoutParams(ViewGroup.LayoutParams params);//适配View

    void addView(View child);//适配View
}
