package com.lt.ltviews.lt_recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lt.ltviews.R;
import com.lt.ltviews.lt_listener.OnNoItemListener;
import com.lt.ltviews.lt_listener.OnUpAndDownListener;

import java.lang.reflect.InvocationTargetException;


/**
 * 所在包名:  com.lt.ltrecyclerview
 * 创建日期:  2017/4/6--17:10
 * 作   用:   以RecyclerView为基础,可以上拉加载和下拉刷新
 * 使用方法:
 * 注意事项:   该包内废弃,改用ltviewsx内的文件
 */

public class LTRecyclerView extends FrameLayout {
    private BaseRefreshLayout refreshLayout;
    private RecyclerView rv;
    private OnUpAndDownListener onUpAndDownListener;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager gridLayoutManager;
    private View noItemView;

    public LTRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public LTRecyclerView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LTRecyclerView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //创建rv
        rv = new RecyclerView(context);
        //添加下拉刷新
        try {
            refreshLayout = thisRefreshLayout();
        } catch (Exception e) {
            throw new RuntimeException("请将刷新的类继承ViewGroup类或子类并实现BaseRefreshLayout接口");
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rv.setLayoutParams(lp);
        refreshLayout.setLayoutParams(lp);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新时,添加下拉刷新回调
                if (onUpAndDownListener != null) {
                    onUpAndDownListener.down();
                }
            }
        });

        refreshLayout.addView(rv);
        this.addView((ViewGroup) refreshLayout);

        //设置自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LTRecyclerView);
        //没有条目时的文字
        String noItemText = a.getString(R.styleable.LTRecyclerView_noItemText);
        if (!TextUtils.isEmpty(noItemText)) {
            setNoItemText(noItemText);
        }
        //没有条目时的布局或图片,或字符串,或颜色...
        int noItemViewId = a.getResourceId(R.styleable.LTRecyclerView_noItemView, 0);
        if (noItemViewId > 0) {
            switch (context.getResources().getResourceTypeName(noItemViewId)) {
                case "layout":
                    setNoItemView(View.inflate(context, noItemViewId, null));
                    break;
                case "mipmap":
                case "drawable":
                    ImageView noItemView = new ImageView(context);
                    noItemView.setImageResource(noItemViewId);
                    setNoItemView(noItemView);
                    break;
                case "string":
                    setNoItemText(getResources().getString(noItemViewId));
                    break;
                case "color":
                    ImageView noItemViewIv = new ImageView(context);
                    noItemViewIv.setBackgroundResource(noItemViewId);
                    setNoItemView(noItemViewIv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    break;
                default:
                    break;
            }
        }
        //分割线高度
        float dHeight = a.getDimension(R.styleable.LTRecyclerView_dividerHeight, 0);
        //分割线颜色
        int dColor = a.getColor(R.styleable.LTRecyclerView_dividerColor, 0xffd5d5d5);
        if (dHeight != 0) {
            addItemDecoration_line((int) dHeight, dColor);
        }
        //分割线图片
        Drawable drawable = a.getDrawable(R.styleable.LTRecyclerView_dividerDrawable);
        if (drawable != null) {
            addItemDecoration_drawable(drawable);
        }
        a.recycle(); //使用完进行回收

        gridLayoutManager = new GridLayoutManager(context, 1);//根据要显示的类型获得 ViewGroup 管理者
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置为竖直的 GridView
        rv.setLayoutManager(gridLayoutManager);//ViewGroup 管理者设置给 view

        //添加上拉加载,这个是滚动的监听
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (onUpAndDownListener == null)//没有回调就没必要走
                        return;
                    if (refreshLayout != null && refreshLayout.isRefreshing())
                        return;
                    if (adapter == null)//如果没有适配器
                        return;
                    if (adapter instanceof LtAdapter && !((LtAdapter) adapter).getIsHaveData())//如果已标记为没数据
                        return;
                    if (gridLayoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                        //如果是最后一个条目,表示是上拉加载
                        onUpAndDownListener.up();
                    }
                }
            }
        });
    }

    /**
     * 类内部获取下拉刷新的View,可以继承并重写该方法来实现项目内不同的下拉刷新效果
     */
    protected BaseRefreshLayout thisRefreshLayout() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = LtRecyclerViewManager.getInstance().getRefreshLayoutClazz();
        if (clazz == MSwipeRefreshLayout.class)
            return new MSwipeRefreshLayout(getContext());
        if (clazz == MTextRefreshLayout.class)
            return new MTextRefreshLayout(getContext());
        return (BaseRefreshLayout) clazz.getConstructor(Context.class).newInstance(getContext());
    }

    /**
     * 设置一行展示多少列
     */
    public @NonNull
    LTRecyclerView setSpanCount(int spanCount) {
        gridLayoutManager.setSpanCount(spanCount);
        return this;
    }

    /**
     * 添加上拉和下拉的回调接口
     *
     * @param onUpAndDownListener 回调接口
     */
    public @NonNull
    LTRecyclerView setOnUpAndDownListener( @Nullable OnUpAndDownListener onUpAndDownListener) {
        this.onUpAndDownListener = onUpAndDownListener;
        return this;
    }

    /**
     * 获取到自定义控件中包含的RecyclerView
     *
     * @return 自定义控件中包含的RecyclerView
     */
    public @NonNull
    RecyclerView getRecyclerView() {
        return rv;
    }

    /**
     * 获得自定义控件中的RefreshLayout
     *
     * @return 自定义控件中的RefreshLayout
     */
    public @NonNull
    BaseRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    /**
     * 设置RecyclerView的适配器
     *
     * @param adapter 继承自RecyclerView.Adapter的适配器
     */
    public @NonNull
    LTRecyclerView setAdapter(@NonNull RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        if (this.adapter instanceof LtAdapter)
            ((LtAdapter) this.adapter).addOnNoItemListener(new OnNoItemListener() {
                @Override
                public void noItem() {
                    //没有条目时隐藏rl,然后展示没条目时的布局
                    rv.setVisibility(View.INVISIBLE);
                    if (noItemView != null) {
                        noItemView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void haveItem() {
                    //有条目了就显示rv,并且隐藏noItemView
                    rv.setVisibility(View.VISIBLE);
                    if (noItemView != null) {
                        noItemView.setVisibility(View.GONE);
                    }
                }
            });
        rv.setAdapter(this.adapter);
        if (this.adapter instanceof LtAdapter)
            if (((LtAdapter) this.adapter).getLtItemCount() == 0 && ((LtAdapter) this.adapter).getHeadListSize() == 0 && ((LtAdapter) this.adapter).getTailListSize() == 0)
                rv.setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 设置没有条目时展示的View,默认是居中的
     */
    public @NonNull
    LTRecyclerView setNoItemView(@NonNull View view) {
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        return setNoItemView(view, lp);
    }

    public @NonNull
    LTRecyclerView setNoItemView(@NonNull View view, @NonNull ViewGroup.LayoutParams layoutParams) {
        if (this.noItemView != null)
            this.removeView(this.noItemView);
        this.noItemView = view;
        noItemView.setLayoutParams(layoutParams);
        if (adapter == null) {
            noItemView.setVisibility(VISIBLE);
        } else {
            if (adapter instanceof LtAdapter && ((LtAdapter) adapter).getLtItemCount() == 0) {
                noItemView.setVisibility(VISIBLE);
            } else if (adapter.getItemCount() == 0) {
                noItemView.setVisibility(VISIBLE);
            } else {
                noItemView.setVisibility(INVISIBLE);
            }
        }
        this.addView(noItemView);
        return this;
    }

    public @NonNull
    LTRecyclerView setNoItemText(@NonNull String text) {
        TextView tv = new TextView(getContext());
        tv.setTextColor(LtRecyclerViewManager.getInstance().getNoItemTextColor());
        tv.setText(text);
        return setNoItemView(tv);
    }

    /**
     * 获取没有条目时展示的View
     */
    public @Nullable
    View getNoItemView() {
        return noItemView;
    }

    /**
     * 刷新rv的布局
     */
    public @NonNull
    LTRecyclerView notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        return this;
    }

    /**
     * 判断适配器是否为空
     */
    public boolean adapterIsNull() {
        return adapter == null;
    }

    /**
     * 获取适配器对象
     */
    public @Nullable
    RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    /**
     * 获取线性多列布局的管理者
     */
    public @NonNull
    GridLayoutManager getLayoutManager() {
        return gridLayoutManager;
    }

    /**
     * 设置是否上拉加载
     */
    public @NonNull
    LTRecyclerView setBottomRefresh(boolean b) {
        if (this.adapter != null)
            if (this.adapter instanceof LtAdapter)
                ((LtAdapter) this.adapter).setRefresh(b);
        return this;
    }

    /**
     * 设置是否下拉刷新
     */
    public @NonNull
    LTRecyclerView setTopRefresh(boolean b) {
        refreshLayout.setRefreshing(b);
        return this;
    }

    /**
     * 设置是否上拉加载(底部)和下拉刷新(顶部)
     */
    public @NonNull
    LTRecyclerView setRefresh(boolean top, boolean bottom) {
        return setTopRefresh(top).setBottomRefresh(bottom);
    }

    /**
     * 添加分割线
     */
    public @NonNull
    LTRecyclerView addItemDecoration_line() {
        return addItemDecoration_line(2);
    }

    public @NonNull
    LTRecyclerView addItemDecoration_line(int px) {
        return addItemDecoration_line(px, 0xffd5d5d5);
    }

    public @NonNull
    LTRecyclerView addItemDecoration_line(int px, int color) {
        rv.addItemDecoration(new LtDivider(rv, px, color));
        return this;
    }

    public @NonNull
    LTRecyclerView addItemDecoration_drawable(int resId) {
        return addItemDecoration_drawable(ContextCompat.getDrawable(getContext(), resId));
    }

    public @NonNull
    LTRecyclerView addItemDecoration_drawable(@NonNull Drawable drawable) {
        rv.addItemDecoration(new LtDivider(rv, drawable));
        return this;
    }
}
