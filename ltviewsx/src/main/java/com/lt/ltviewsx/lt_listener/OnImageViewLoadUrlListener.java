package com.lt.ltviewsx.lt_listener;

import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 创    建:  lt  2019/2/12--10:31
 * 作    用:  给ImageView加载网络图片的回调
 * 注意事项:
 */
public interface OnImageViewLoadUrlListener {
    void onLoad(@NotNull ImageView iv, @Nullable String url);
}
