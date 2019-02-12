package com.lt.ltviews.lt_listener;

import android.widget.ImageView;

/**
 * 创    建:  lt  2019/2/12--10:31
 * 作    用:  给ImageView加载网络图片的回调
 * 注意事项:
 */
public interface OnImageViewLoadUrlListener {
    void onLoad(ImageView iv, String url);
}
