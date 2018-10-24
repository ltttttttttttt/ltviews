package com.lt.ltviews.lt_scrollimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lt.ltviews.lt_listener.OnRvItemClickListener;
import com.lt.ltviews.lt_listener.OnScrollListener;

/**
 * 创    建:  lt  2018/1/4--14:12
 * 作    用:
 * 注意事项:
 */

public class LtScrollImageView extends FrameLayout {

    private LtAdGallery ltAdGallery;
    private LinearLayout ll;

    public LtScrollImageView(Context context) {
        this(context, null);
    }

    public LtScrollImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LtScrollImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ltAdGallery = new LtAdGallery(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ltAdGallery.setLayoutParams(lp);
        addView(ltAdGallery);
        ll = new LinearLayout(context);
        LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ll.setPadding(30, 20, 30, 20);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        lp2.gravity = Gravity.BOTTOM;
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(lp2);
        addView(ll);
    }

    /**
     * 初始化
     *
     * @param aTime        中间过度的动画播放时间
     * @param imageUrl     图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime   图片切换时间 写0 为不自动切换
     * @param focusedId    圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId     圆点正常时的背景ID,圆点容器为空写0
     * @param backgroundId 默认图或加载失败时的图
     */
    public LtScrollImageView init(String[] imageUrl, int switchTime, int aTime, int focusedId, int normalId, int backgroundId) {
        ltAdGallery.start(getContext(), imageUrl, switchTime, aTime, ll, focusedId, normalId, backgroundId);
        return this;
    }

    /**
     * 设置条目点击事件
     */
    public LtScrollImageView setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        ltAdGallery.setOnRvItemClickListener(onRvItemClickListener);
        return this;
    }

    /**
     * 设置小圆点的左右距离
     */
    public LtScrollImageView setIvMargin(int margin) {
        ltAdGallery.setIvMargin(margin);
        return this;
    }

    /**
     * 设置滚动条目监听
     */
    public LtScrollImageView setOnScrollListener(OnScrollListener listener) {
        ltAdGallery.setOnScrollListener(listener);
        return this;
    }

    /**
     * 设置小圆点的九个位置,可以加一个枚举 todo
     */
    public LtScrollImageView setPosition(LtPosition ltSIVPosition) {
        if (ltSIVPosition == LtPosition.LEFT)
            ll.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        else if (ltSIVPosition == LtPosition.RIGHT)
            ll.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        else if (ltSIVPosition == LtPosition.CENTER)
            ll.setGravity(Gravity.CENTER);
        return this;
    }
}
