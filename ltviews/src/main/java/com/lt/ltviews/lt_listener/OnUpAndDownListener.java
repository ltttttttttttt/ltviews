package com.lt.ltviews.lt_listener;

/**
 * 所在包名:  com.lt.ltrecyclerview
 * 创建日期:  2017/4/22--9:55
 * 作   用:   上拉加载和下拉刷新的回调接口
 * 使用方法:
 * 注意事项:
 */

public interface OnUpAndDownListener {
    /**
     * 上拉加载
     */
    void up();

    /**
     * 下拉刷新
     */
    void down();
}
