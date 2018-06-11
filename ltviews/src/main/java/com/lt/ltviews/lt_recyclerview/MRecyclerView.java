package com.lt.ltviews.lt_recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 创    建:  lt  2018/5/24--16:20
 * 作    用:  兼容下拉加载的RecyclerView
 * 注意事项:
 */

public class MRecyclerView extends RecyclerView {

    GridLayoutManager glm = null;//布局管理器
    float y = 0;
    boolean isSwl;//是否是srl
    ViewGroup vg = null;//父view,也就是刷新view的容器
    boolean rvIsMove;//RecyclerView是否跟着下拉移动
    View v = null;//刷新view

    public MRecyclerView(Context context) {
        this(context, null);
    }

    public MRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isSwl = LtRecyclerViewManager.create().getRefreshLayoutClazz().isAssignableFrom(MSwipeRefreshLayout.class);
        rvIsMove = LtRecyclerViewManager.create().isRvIsMove();//设置rv是否移动
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isSwl) return super.onInterceptTouchEvent(e);
        if (glm == null) {
            glm = (GridLayoutManager) getLayoutManager();
        }
        return e.getAction() == MotionEvent.ACTION_MOVE || super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isSwl) return super.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (glm.findFirstVisibleItemPosition() == 0 && glm.getChildAt(0).getY() >= 0) {//是第一个条目
                    float newY = e.getY();
                    if (rvIsMove) {
                        if (y > newY && getTranslationY() <= 0) {//表示是第一个条目,并且没有下拉出来,并且往上滑动的
                            y = newY;
                            break;
                        }
                    } else {//rv不移动的时候
                        if (vg == null)
                            vg = ((ViewGroup) getParent());
                        if (v == null && vg.getChildCount() >= 1)
                            v = vg.getChildAt(1);
                        if (y > newY && (v == null || v.getTranslationY() <= 0)) {//表示是往上滑动的,并且刷新view的ty<=0,才能走这一步
                            y = newY;
                            break;
                        }
                    }
                    if (vg == null)
                        vg = ((ViewGroup) getParent());
                    y = newY;
                    vg.onTouchEvent(e);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (glm.findFirstVisibleItemPosition() == 0 && glm.getChildAt(0).getY() >= 0) {//是第一个条目
                    if (vg == null)
                        vg = ((ViewGroup) getParent());
                    vg.onTouchEvent(e);
                    return false;
                }
                break;
        }
        return super.onTouchEvent(e);
    }
}
