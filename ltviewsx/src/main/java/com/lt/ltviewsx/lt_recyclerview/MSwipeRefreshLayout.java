package com.lt.ltviewsx.lt_recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lt.ltviewsx.R;


/**
 * 创    建:  lt  2018/5/23--17:52
 * 作    用:  兼容的下拉刷新控件
 * 注意事项:
 */

public class MSwipeRefreshLayout extends SwipeRefreshLayout implements BaseRefreshLayout {

    public MSwipeRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public MSwipeRefreshLayout(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.colorAccent);
    }
}
