package com.lt.ltviews.lt_recyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 创    建:  lt  2018/5/23--18:09
 * 作    用:  自定义的下拉刷新View的父类
 * 注意事项:
 */

public abstract class LtRefreshLayout extends FrameLayout implements BaseRefreshLayout {
    boolean isRefresh = false;//是否刷新的状态,用户使用
    boolean rvIsMove;//RecyclerView是否跟着下拉移动
    SwipeRefreshLayout.OnRefreshListener listener;//刷新的回调
    float y = 0, fastY = -1.0f;//当前的y和第一次按下的y(近似)
    RecyclerView rv;//内部的rv
    View refreshView;//刷新的view
    float refreshThreshold;//下拉刷新位置的阈值
    int status;//当前状态值
    int animationTime = 300, waitTime = 500;//动画时间和等待时间

    public final static int REFRESH_DOWN = 0;//下拉中
    public final static int REFRESH_RELEASE = 1;//松开刷新
    public final static int REFRESHING = 2;//刷新中
    public final static int REFRESH_FINISH = 3;//刷新完成

    /**
     * 当状态变更时调用,在此方法中更改刷新View的状态
     *
     * @param status 状态值
     */
    public abstract void onStatus(int status);

    /**
     * 请在此方法内做额外操作
     *
     * @param y 当前下拉的y轴
     */
    public abstract void onProgress(float y);

    /**
     * 返回刷新的View
     */
    public abstract View getRefreshView();

    public LtRefreshLayout() {
        this(LtRecyclerViewManager.create().getContext());
    }

    public LtRefreshLayout(Context context) {
        this(context, null);
    }

    public LtRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LtRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshThreshold = LtRecyclerViewManager.create().getRefreshThreshold();//设置阈值
        rvIsMove = LtRecyclerViewManager.create().isRvIsMove();//设置rv是否移动
        this.y = rvIsMove ? 9999 : 0;//设置第一次的y
    }

    /**
     * 设置刷新时的回调
     */
    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        this.listener = listener;
    }

    /**
     * 设置是否刷新
     */
    @Override
    public void setRefreshing(boolean refreshing) {
        if (refreshing == isRefresh)
            return;
        isRefresh = refreshing;
        if (!isRefresh && status == REFRESHING) {//如果isr变为false,并且当前状态为刷新中,则更改为刷新完成(阈值处停留200,然后300缩回去)
            status = REFRESH_FINISH;
            onStatus(status);
            isRefresh = false;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress(0, animationTime);
                    ObjectAnimator.ofFloat(rv, "translationY", rv.getTranslationY(), 0).setDuration(animationTime).start();
                }
            }, waitTime);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    status = REFRESH_DOWN;
                    onStatus(status);
                }
            }, animationTime + waitTime);
            if (!rvIsMove) {
                fastY = -1.0f;
                y = 0;
            }
        } else if (isRefresh && status != REFRESHING) {//如果isr变为true,并且当前状态不是刷新中状态,变更为刷新中状态,rv和刷新view置为-阈值
            status = REFRESHING;
            onStatus(status);
            progress(refreshThreshold, animationTime);
            ObjectAnimator.ofFloat(rv, "translationY", rv.getTranslationY(), refreshThreshold).setDuration(animationTime).start();
            if (listener != null)
                listener.onRefresh();
        }
    }

    /**
     * 下拉的进度
     */
    void progress(float y, int time) {
        if (time == 0) {
            if (rvIsMove)//todo 如果rv跟着动,就调用这个,rv不动的时候在进行测试
                refreshView.setTranslationY(rv.getTranslationY());
            else
                refreshView.setTranslationY(y / 2 + refreshView.getTranslationY());
            onProgress(y);
            if (rvIsMove) {//rv是否移动对阈值的计算有影响
                if ((this.y + y - fastY) / 2 < refreshThreshold && status == REFRESH_RELEASE) {//如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    status = REFRESH_DOWN;
                    onStatus(status);
                } else if ((this.y + y - fastY) / 2 >= refreshThreshold && status == REFRESH_DOWN) {//如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    status = REFRESH_RELEASE;
                    onStatus(status);
                }
            } else {
                if (this.y + y - fastY < refreshThreshold && status == REFRESH_RELEASE) {//如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    status = REFRESH_DOWN;
                    onStatus(status);
                } else if (this.y + y - fastY >= refreshThreshold && status == REFRESH_DOWN) {//如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    status = REFRESH_RELEASE;
                    onStatus(status);
                }
            }
        } else {//启用定时操作
            ValueAnimator va = ValueAnimator.ofFloat(refreshView.getTranslationY(), y)
                    .setDuration(time);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    refreshView.setTranslationY(f);
                    onProgress(f);
                }
            });
            va.start();//执行这个数值变化器
            if (y != 0.0f && status != REFRESHING) {//有时间并且不为0,表示会跳到阈值,如果不是刷新中状态就改为刷新中状态
                status = REFRESHING;
                onStatus(status);
                isRefresh = true;
                if (listener != null)
                    listener.onRefresh();
            }
        }
    }

    /**
     * 用来添加rv,会自动添加刷新的view,只能调用一次
     */
    @Override
    public void addView(View child) {
        if (getChildCount() != 0)
            return;
        super.addView(child);
        this.rv = (RecyclerView) child;
        this.refreshView = getRefreshView();
        super.addView(refreshView);
        //设置为负的阈值位置
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) refreshThreshold);
        lp.topMargin = -(int) refreshThreshold;
        refreshView.setLayoutParams(lp);
    }

    /**
     * 获取是否刷新
     */
    @Override
    public boolean isRefreshing() {
        return isRefresh;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //实现触摸响应
        if (!isEnabled())//如果不启用下拉则结束
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float newY = event.getY();
                if (fastY == -1.0f) {
                    fastY = newY;
                }
                float distance = newY - y;
                if (rvIsMove)//需要rv向下移动时则移动下拉一半的距离
                    rv.setTranslationY(rv.getTranslationY() >= 0 ? distance / 2 + rv.getTranslationY() : 0);
                if (y != 0.0f)
                    progress(rv.getTranslationY() >= 0 ? distance : 0, 0);
                else//y==0
                    progress(rv.getTranslationY() + refreshThreshold, 0);
                if (y > newY) {//交给子去执行事件
                    y = newY;
                    rv.onInterceptTouchEvent(event);
                    rv.onTouchEvent(event);
                    return false;
                }
                y = newY;
                return true;
            case MotionEvent.ACTION_UP:
                //如果是松开或者刷新状态,移动到阈值,否则归0
                if (rvIsMove) {//如果rv可以下移,则离开屏幕是回归原位
                    if (status == REFRESHING || status == REFRESH_RELEASE)
                        ObjectAnimator.ofFloat(rv, "translationY", rv.getTranslationY(), refreshThreshold).setDuration(animationTime).start();
                    else
                        ObjectAnimator.ofFloat(rv, "translationY", rv.getTranslationY(), 0).setDuration(animationTime).start();
                    fastY = -1.0f;
                }
                if (status == REFRESHING || status == REFRESH_RELEASE) {
                    progress(refreshThreshold, animationTime);
                } else {
                    progress(0, animationTime);
                    if (!rvIsMove) {
                        fastY = -1.0f;
                        y = 0;
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}
