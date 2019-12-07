package com.lt.ltviewsx.lt_recyclerview;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lt.ltviewsx.R;


/**
 * 创    建:  lt  2018/5/22--15:23
 * 作    用:  ltRv的管理者
 * 注意事项:
 */

final public class LtRecyclerViewManager {
    private Context context;//创建刷新布局时的上下文,必须有
    private Class refreshLayoutClazz = MSwipeRefreshLayout.class;//刷新布局的class
    private int upLayoutId = R.layout.lt_up_loading;//上拉加载的布局id
    private float refreshThreshold;//设置下拉的阈值
    private boolean rvIsMove = true;//RecyclerView是否跟着下拉移动
    private boolean noDataIsLoad = false;//上拉已经没数据了,再次上拉是否加载数据
    private int noItemTextColor = 0;//没条目的tv的字体颜色

    //单例
    private LtRecyclerViewManager() {
    }

    private static class LtRecyclerViewManagerInstance {
        private static final LtRecyclerViewManager mLtRecyclerViewManager = new LtRecyclerViewManager();
    }

    /**
     * 获取单实例
     */
    public static @NonNull
    LtRecyclerViewManager getInstance() {
        return LtRecyclerViewManagerInstance.mLtRecyclerViewManager;
    }

    /**
     * 初始化,在application的onCreate中调用
     */
    public @NonNull
    LtRecyclerViewManager init(@NonNull Context context) {
        this.context = context;
        this.refreshThreshold = 80 * context.getResources().getDisplayMetrics().density;
        noItemTextColor = context.getResources().getColor(R.color.color_333);
        return this;
    }

    /**
     * 获取没条目的tv的字体颜色
     */
    public int getNoItemTextColor() {
        return noItemTextColor;
    }

    /**
     * 设置没条目的tv的字体颜色
     */
    public void setNoItemTextColor(int noItemTextColor) {
        this.noItemTextColor = noItemTextColor;
    }

    /**
     * 获取实例化时传入的上下文
     */
    public @NonNull
    Context getContext() {
        return context;
    }

    /**
     * 设置下拉刷新的View的class
     */
    public @NonNull
    LtRecyclerViewManager setRefreshLayoutClazz(@NonNull Class refreshLayoutClazz) {
        this.refreshLayoutClazz = refreshLayoutClazz;
        return this;
    }

    /**
     * 获取下拉刷新的View的class
     */
    public @Nullable
    Class getRefreshLayoutClazz() {
        return refreshLayoutClazz;
    }

    /**
     * 设置上拉加载的布局id
     */
    public @NonNull
    LtRecyclerViewManager setUpLayoutId(int upLayoutId) {
        this.upLayoutId = upLayoutId;
        return this;

    }

    /**
     * 获取上拉加载的布局id
     */
    public int getUpLayoutId() {
        return upLayoutId;
    }

    /**
     * 获取下拉阈值
     */
    public float getRefreshThreshold() {
        return refreshThreshold;
    }

    /**
     * 设置下拉阈值
     */
    public @NonNull
    LtRecyclerViewManager setRefreshThreshold(float refreshThreshold) {
        this.refreshThreshold = refreshThreshold;
        return this;
    }

    /**
     * 重新设置上下文
     */
    public @NonNull
    LtRecyclerViewManager setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    /**
     * 获取是否下拉时RecyclerView跟着向下移动
     */
    public boolean isRvIsMove() {
        return rvIsMove;
    }

    /**
     * 设置是否下拉时RecyclerView跟着向下移动
     */
    public @NonNull
    LtRecyclerViewManager setRvIsMove(boolean rvIsMove) {
        this.rvIsMove = rvIsMove;
        return this;
    }

    /**
     * 上拉已经没数据了,再次上拉是否加载数据
     */
    public boolean isNoDataIsLoad() {
        return noDataIsLoad;
    }

    /**
     * 上拉已经没数据了,再次上拉是否加载数据,默认false不加载
     */
    public @NonNull
    LtRecyclerViewManager setNoDataIsLoad(boolean noDataIsLoad) {
        this.noDataIsLoad = noDataIsLoad;
        return this;
    }
}
