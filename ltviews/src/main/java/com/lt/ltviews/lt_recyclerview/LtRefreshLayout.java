package com.lt.ltviews.lt_recyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
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
    protected boolean rvIsMove;//View是否跟着下拉移动
    protected SwipeRefreshLayout.OnRefreshListener listener;//刷新的回调
    protected float y = 0, fastY = -1.0f;//当前的y和第一次按下的y(近似)
    protected View contentView;//内部的view
    protected View refreshView;//刷新的view
    protected float refreshThreshold;//下拉刷新位置的阈值
    protected int state = STATE_BACK;//当前状态值
    protected int animationTime = 300, waitTime = 500;//动画时间和等待时间
    protected float mLastY;//判断拦截事件用的第一次触摸的y轴

    public final static int STATE_REFRESH_DOWN = 0;//下拉中
    public final static int STATE_REFRESH_RELEASE = 1;//松开刷新
    public final static int STATE_REFRESHING = 2;//刷新中
    public final static int STATE_REFRESH_FINISH = 3;//刷新完成
    public final static int STATE_BACK = 4;//刷新结束,并且刷新View隐藏到了顶部

    /**
     * 当状态变更时调用,在此方法中更改刷新View的状态
     *
     * @param status 状态值
     */
    protected abstract void onStatus(int status);

    /**
     * 请在此方法内做额外操作
     *
     * @param y 当前下拉的y轴
     */
    protected abstract void onProgress(float y);

    /**
     * 返回刷新的View
     */
    protected abstract View getRefreshView();

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
        if (refreshing && state == STATE_REFRESHING) {
            //如果设置为刷新中,如果当前是刷新中则返回
            return;
        }
        if ((!refreshing) && state != STATE_REFRESHING) {
            //如果设置为刷新完成,如果当前不是刷新中的状态,则返回
            return;
        }
        if (state == STATE_REFRESHING) {//如果isr变为false,并且当前状态为刷新中,则更改为刷新完成(阈值处停留200,然后300缩回去)
            state = STATE_REFRESH_FINISH;
            onStatus(state);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress(0, animationTime);
                    ObjectAnimator.ofFloat(contentView, "translationY", contentView.getTranslationY(), 0).setDuration(animationTime).start();
                }
            }, waitTime);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    state = STATE_BACK;
                    onStatus(state);
                }
            }, animationTime + waitTime);
            if (!rvIsMove) {
                fastY = -1.0f;
                y = 0;
            }
        } else {//如果isr变为true,并且当前状态不是刷新中状态,变更为刷新中状态,rv和刷新view置为-阈值
            state = STATE_REFRESHING;
            onStatus(state);
            progress(refreshThreshold, animationTime);
            ObjectAnimator.ofFloat(contentView, "translationY", contentView.getTranslationY(), refreshThreshold).setDuration(animationTime).start();
            if (listener != null)
                listener.onRefresh();
        }
    }

    /**
     * 下拉的进度
     */
    protected void progress(float y, int time) {
        if (time == 0) {
            if (rvIsMove)//如果rv跟着动,就调用这个
                refreshView.setTranslationY(contentView.getTranslationY());
            else
                refreshView.setTranslationY(y / 2 + refreshView.getTranslationY());
            onProgress(y);
            if (state == STATE_BACK) {//如果下拉的时候,状态是back,则改为下拉中
                state = STATE_REFRESH_DOWN;
                onStatus(state);
            }
            if (rvIsMove) {//rv是否移动对阈值的计算有影响
                if ((this.y + y - fastY) / 2 < refreshThreshold && state == STATE_REFRESH_RELEASE) {//如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    state = STATE_REFRESH_DOWN;
                    onStatus(state);
                } else if ((this.y + y - fastY) / 2 >= refreshThreshold && state == STATE_REFRESH_DOWN) {//如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    state = STATE_REFRESH_RELEASE;
                    onStatus(state);
                }
            } else {
                if (this.y + y - fastY < refreshThreshold && state == STATE_REFRESH_RELEASE) {//如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    state = STATE_REFRESH_DOWN;
                    onStatus(state);
                } else if (this.y + y - fastY >= refreshThreshold && state == STATE_REFRESH_DOWN) {//如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    state = STATE_REFRESH_RELEASE;
                    onStatus(state);
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
            if (y != 0.0f && state != STATE_REFRESHING) {//有时间并且不为0,表示会跳到阈值,如果不是刷新中状态就改为刷新中状态
                state = STATE_REFRESHING;
                onStatus(state);
                if (listener != null)
                    listener.onRefresh();
            }
            if (y == 0.0f && state == STATE_REFRESH_DOWN) {
                //如果还原到0,并且还是下拉中,说明不需要刷新只需要还原状态
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        state = STATE_BACK;
                        onStatus(state);
                    }
                }, animationTime);
            }
        }
    }

    /**
     * 用来添加rv,会自动添加刷新的view,只能调用一次
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 2)
            throw new RuntimeException("this method can only be called once!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.addView(child, index, params);
        if (getChildCount() == 2)
            return;
        this.contentView = child;
        this.refreshView = getRefreshView();
        //设置为负的阈值位置
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) refreshThreshold);
        lp.topMargin = -(int) refreshThreshold;
        refreshView.setLayoutParams(lp);
        super.addView(refreshView);
    }

    /**
     * 获取是否刷新
     */
    @Override
    public boolean isRefreshing() {
        return state == STATE_REFRESHING;
    }

    /**
     * 是否拦截触摸事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || state != STATE_BACK)//如果不启用下拉则结束,或者刷新view已经出来了
            return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mCurY = ev.getY();
                int mark = (int) (mCurY - mLastY);
                mLastY = mCurY;
                //如果contentView内容在Y轴上可滑动，把事件传递给contentView内部
                if (contentView.canScrollVertically(-mark))
                    return false;
                //如果滑动的距离小于10px，我们认为这次滑动是无效的，把这次事件传递给contentView去消费。例如contentView的child的点击事件。
                //如果大于等于10px，我们把这次事件给拦截掉，让这次事件在onTouchEvent(MotionEvent ev)方法中消费掉。
//                if (Math.abs(mark) >= 10)
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 分发触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //实现触摸响应
        if (!isEnabled() || state == STATE_REFRESHING || state == STATE_REFRESH_FINISH)//如果不启用下拉则结束,或者刷新中和刷新完成阶段
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float newY = event.getY();
                if (fastY == -1.0f) {
                    fastY = newY;
                }
                float distance = newY - y;
                if (distance < 0) {
                    //表示向上推
                    //如果向上推,刷新的view还没动,则不使用
                    if (refreshView.getTranslationY() == 0) {
                        y = newY;
                        return false;
                    }
                }
                if (rvIsMove)//需要rv向下移动时则移动下拉一半的距离
                    contentView.setTranslationY(contentView.getTranslationY() >= 0 ? distance / 2 + contentView.getTranslationY() : 0);
                if (y != 0.0f)
                    progress(contentView.getTranslationY() >= 0 ? distance : 0, 0);
                else//y==0
                    progress(contentView.getTranslationY() + refreshThreshold, 0);
                if (y > newY) {//交给子去执行事件
                    y = newY;
                    if (state == STATE_BACK)
                        contentView.onTouchEvent(event);
                    return false;
                }
                y = newY;
                return true;
            case MotionEvent.ACTION_UP:
                //如果是松开或者刷新状态,移动到阈值,否则归0
                if (rvIsMove) {//如果rv可以下移,则离开屏幕是回归原位
                    if (state == STATE_REFRESHING || state == STATE_REFRESH_RELEASE)
                        ObjectAnimator.ofFloat(contentView, "translationY", contentView.getTranslationY(), refreshThreshold).setDuration(animationTime).start();
                    else {
                        ObjectAnimator.ofFloat(contentView, "translationY", contentView.getTranslationY(), 0).setDuration(animationTime).start();
                    }
                    fastY = -1.0f;
                }
                if (state == STATE_REFRESHING || state == STATE_REFRESH_RELEASE) {
                    progress(refreshThreshold, animationTime);
                    fastY = -1.0f;
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
