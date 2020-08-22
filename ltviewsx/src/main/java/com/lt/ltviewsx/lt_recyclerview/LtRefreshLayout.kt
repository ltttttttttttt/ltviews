package com.lt.ltviewsx.lt_recyclerview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lt.ltviewsx.R

/**
 * 创    建:  lt  2018/5/23--18:09
 * 作    用:  自定义的下拉刷新View的父类
 * 注意事项:
 */
abstract class LtRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr),
        BaseRefreshLayout {
    protected var rvIsMove = LtRecyclerViewManager.isRvIsMove //View是否跟着下拉移动
    protected var listener: SwipeRefreshLayout.OnRefreshListener? = null //刷新的回调
    protected var yAxis = if (rvIsMove) 9999F else 0F //y轴的值
    protected var fastY = -1.0F //当前的y和第一次按下的y(近似)
    protected lateinit var contentView: View//内部的view
    protected lateinit var refreshView: View//刷新的view
    protected var refreshThreshold = LtRecyclerViewManager.refreshThreshold //下拉刷新位置的阈值
    protected var state = RefreshStates.STATE_BACK //当前状态值
    protected var animationTime = 300L//动画时间
    protected var waitTime = 500L//下拉回弹的等待时间
    protected var mLastY = 0F//判断拦截事件用的第一次触摸的y轴 = 0F
    protected var refreshViewHeight = refreshThreshold.toInt()//设置刷新View的高度
    protected var scrollOrClickBoundary = context.resources.getDimension(R.dimen.dp4)//判断是滚动或者点击的边界,一般是4dp(点击的半径),用来判断本次滑动是否有效,防止阻断掉点击事件
    protected val noItemView by lazy(LazyThreadSafetyMode.NONE) { (parent as? LTRecyclerView)?.noItemView }//如果整体需要下滑,就需要拿到noItemView

    /**
     * 刷新状态
     */
    protected enum class RefreshStates {
        STATE_REFRESH_DOWN, //下拉中
        STATE_REFRESH_RELEASE, //松开刷新
        STATE_REFRESHING, //刷新中
        STATE_REFRESH_FINISH, //刷新完成
        STATE_BACK  //刷新结束,并且刷新View隐藏到了顶部
    }

    /**
     * 当状态变更时调用,在此方法中更改刷新View的状态
     *
     * @param state 状态值
     */
    protected abstract fun onState(state: RefreshStates)

    /**
     * 返回创建完成的刷新的View,一般只会调用一次
     */
    protected abstract fun createRefreshView(): View

    /**
     * 请在此方法内做额外操作
     *
     * @param y 当前下拉的y轴
     */
    protected open fun onProgress(y: Float) {
        //如果整体需要下滑,就把noItemView也往下滑
        if (rvIsMove) {
            noItemView?.translationY = y
        }
    }

    /**
     * 用来设置刷新view的宽高等信息
     */
    protected open fun createRefreshViewLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, refreshViewHeight)
    }

    /**
     * 设置刷新时的回调
     */
    override fun setOnRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener?) {
        this.listener = listener
    }

    /**
     * 获取是否刷新
     */
    override fun isRefreshing(): Boolean = state == RefreshStates.STATE_REFRESHING

    /**
     * 设置是否刷新
     */
    override fun setRefreshing(refreshing: Boolean) {
        if (refreshing && state == RefreshStates.STATE_REFRESHING) {
            //如果设置为刷新中,如果当前是刷新中则返回
            return
        }
        if (!refreshing && state != RefreshStates.STATE_REFRESHING) {
            //如果设置为刷新完成,如果当前不是刷新中的状态,则返回
            return
        }
        if (state == RefreshStates.STATE_REFRESHING) { //如果isr变为false,并且当前状态为刷新中,则更改为刷新完成(阈值处停留200,然后300缩回去)
            state = RefreshStates.STATE_REFRESH_FINISH
            onState(state)
            postDelayed({
                progress(0F, animationTime)
                ObjectAnimator.ofFloat(contentView, "translationY", contentView.translationY, 0F).setDuration(animationTime).start()
            }, waitTime)
            postDelayed({
                state = RefreshStates.STATE_BACK
                onState(state)
            }, animationTime + waitTime)
            if (!rvIsMove) {
                fastY = -1.0F
                yAxis = 0F
            }
        } else { //如果isr变为true,并且当前状态不是刷新中状态,变更为刷新中状态,rv和刷新view置为-阈值
            state = RefreshStates.STATE_REFRESHING
            onState(state)
            progress(refreshThreshold, animationTime)
            ObjectAnimator.ofFloat(contentView, "translationY", contentView.translationY, refreshThreshold).setDuration(animationTime).start()
            listener?.onRefresh()
        }
    }

    /**
     * 下拉的进度
     */
    protected open fun progress(y: Float, time: Long) {
        if (time == 0L) {
            if (rvIsMove) {
                //如果rv跟着动,就调用这个
                val translationY = contentView.translationY
                refreshView.translationY = translationY
                onProgress(translationY)
            } else {
                val translationY = y / 2 + refreshView.translationY
                refreshView.translationY = translationY
                onProgress(translationY)
            }
            if (state == RefreshStates.STATE_BACK) { //如果下拉的时候,状态是back,则改为下拉中
                state = RefreshStates.STATE_REFRESH_DOWN
                onState(state)
            }
            if (rvIsMove) { //rv是否移动对阈值的计算有影响
                if ((this.yAxis + y - fastY) / 2 < refreshThreshold && state == RefreshStates.STATE_REFRESH_RELEASE) { //如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    state = RefreshStates.STATE_REFRESH_DOWN
                    onState(state)
                } else if ((this.yAxis + y - fastY) / 2 >= refreshThreshold && state == RefreshStates.STATE_REFRESH_DOWN) { //如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    state = RefreshStates.STATE_REFRESH_RELEASE
                    onState(state)
                }
            } else {
                if (this.yAxis + y - fastY < refreshThreshold && state == RefreshStates.STATE_REFRESH_RELEASE) { //如果y小于阈值并且不是下拉中状态,改为下拉中状态
                    state = RefreshStates.STATE_REFRESH_DOWN
                    onState(state)
                } else if (this.yAxis + y - fastY >= refreshThreshold && state == RefreshStates.STATE_REFRESH_DOWN) { //如果y大于等于阈值,并且不是松开刷新状态,改为松开刷新状态
                    state = RefreshStates.STATE_REFRESH_RELEASE
                    onState(state)
                }
            }
        } else { //启用定时操作
            val va = ValueAnimator.ofFloat(refreshView.translationY, y)
                    .setDuration(time)
            va.addUpdateListener { animation ->
                val f = animation.animatedValue as Float
                refreshView.translationY = f
                onProgress(f)
            }
            va.start() //执行这个数值变化器
            if (y != 0.0f && state != RefreshStates.STATE_REFRESHING) { //有时间并且不为0,表示会跳到阈值,如果不是刷新中状态就改为刷新中状态
                state = RefreshStates.STATE_REFRESHING
                onState(state)
                listener?.onRefresh()
            }
            if (y == 0.0f && state == RefreshStates.STATE_REFRESH_DOWN) {
                //如果还原到0,并且还是下拉中,说明不需要刷新只需要还原状态
                postDelayed({
                    state = RefreshStates.STATE_BACK
                    onState(state)
                }, animationTime)
            }
        }
    }

    /**
     * 用来添加rv,会自动添加刷新的view,只能调用一次(只能显示设置一个child)
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 2) throw RuntimeException("BaseRefreshLayout.addView method can only be called once!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        if (childCount == 0) {
            contentView = child
            super.addView(child, 0, params)
            refreshView = createRefreshView()
            val lp = createRefreshViewLayoutParams()
            //设置为负的阈值位置
            lp.topMargin = -refreshViewHeight
            super.addView(refreshView, 1, lp)
        } else if (childCount == 0) {
            super.addView(child, index, params)
        }
    }

    /**
     * 是否拦截触摸事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled)
            return false
        if (state != RefreshStates.STATE_BACK) //如果不启用下拉则结束,或者刷新view已经出来了
            return true
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mLastY = ev.y
            MotionEvent.ACTION_MOVE -> {
                val mCurY = ev.y
                val mark = (mCurY - mLastY).toInt()
                mLastY = mCurY
                //如果是向上滑动,或者向下滑动的距离小于4dp(点击事件半径)(可能是点击的时候位移了)，我们认为这次滑动是无效的，把这次事件传递给contentView去消费。例如contentView的child的点击事件。
                //或者contentView内容在Y轴上可滑动，把事件传递给contentView内部(false是不拦截,所以使用!)
                //表示是否要拦截
                val isIntercept = !(mark <= scrollOrClickBoundary || contentView.canScrollVertically(-mark))
                //提示父控件自身要使用本次触摸事件,不要拦截
                parent.requestDisallowInterceptTouchEvent(isIntercept)
                return isIntercept
            }
        }
        return false
    }

    /**
     * 分发触摸事件
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //实现触摸响应
        if (state == RefreshStates.STATE_REFRESHING || state == RefreshStates.STATE_REFRESH_FINISH) //如果不启用下拉则结束,或者刷新中和刷新完成阶段
            return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //如果子View被隐藏则拦截事件
                if (contentView.visibility != View.VISIBLE) return true
                val newY = event.y
                if (fastY == -1.0f) {
                    fastY = newY
                }
                val distance = newY - yAxis
                if (distance < 0) {
                    //表示向上推
                    //如果向上推,刷新的view还没动,则不使用
                    if (refreshView.translationY == 0f) {
                        yAxis = newY
                        return false
                    }
                }
                if (rvIsMove) //需要rv向下移动时则移动下拉一半的距离
                    contentView.translationY = if (contentView.translationY >= 0) distance / 2 + contentView.translationY else 0F
                if (yAxis != 0.0f) progress(if (contentView.translationY >= 0) distance else 0F, 0) else  //y==0
                    progress(contentView.translationY + refreshThreshold, 0)
                if (yAxis > newY) { //交给子去执行事件
                    yAxis = newY
                    if (state == RefreshStates.STATE_BACK) contentView.onTouchEvent(event)
                    return false
                }
                yAxis = newY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val newY = event.y
                if (fastY == -1.0f) {
                    fastY = newY
                }
                val distance = newY - yAxis
                if (distance < 0) {
                    if (refreshView.translationY == 0f) {
                        yAxis = newY
                        return false
                    }
                }
                if (rvIsMove) contentView.translationY = if (contentView.translationY >= 0) distance / 2 + contentView.translationY else 0F
                if (yAxis != 0.0f) progress(if (contentView.translationY >= 0) distance else 0F, 0) else progress(contentView.translationY + refreshThreshold, 0)
                if (yAxis > newY) {
                    yAxis = newY
                    if (state == RefreshStates.STATE_BACK) contentView.onTouchEvent(event)
                    return false
                }
                yAxis = newY
                return true
            }
            MotionEvent.ACTION_UP -> {
                //如果是松开或者刷新状态,移动到阈值,否则归0
                if (rvIsMove) { //如果rv可以下移,则离开屏幕是回归原位
                    if (state == RefreshStates.STATE_REFRESHING || state == RefreshStates.STATE_REFRESH_RELEASE)
                        ObjectAnimator.ofFloat(contentView, "translationY", contentView.translationY, refreshThreshold).setDuration(animationTime).start()
                    else
                        ObjectAnimator.ofFloat(contentView, "translationY", contentView.translationY, 0f).setDuration(animationTime).start()
                    fastY = -1.0f
                }
                if (state == RefreshStates.STATE_REFRESHING || state == RefreshStates.STATE_REFRESH_RELEASE) {
                    progress(refreshThreshold, animationTime)
                    fastY = -1.0f
                } else {
                    progress(0f, animationTime)
                    if (!rvIsMove) {
                        fastY = -1.0f
                        yAxis = 0f
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}