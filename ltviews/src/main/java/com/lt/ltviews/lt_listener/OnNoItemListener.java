package com.lt.ltviews.lt_listener;

/**
 * 所在包名:  com.lt.ltrecyclerviewtest
 * 创建日期:  2017/5/9--17:38
 * 作   用:
 * 使用方法:
 * 注意事项:
 */

public interface OnNoItemListener {
    /**
     * 适配器内没有条目时调用
     */
    void noItem();

    /**
     * 适配器内有数据时调用
     */
    void haveItem();
}
