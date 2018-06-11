package com.lt.ltviews.lt_recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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


/**
 * 所在包名:  com.lt.ltrecyclerview
 * 创建日期:  2017/4/6--17:10
 * 作   用:   以RecyclerView为基础,可以上拉加载和下拉刷新
 * 使用方法:
 * 注意事项:
 */

public class LTRecyclerView extends FrameLayout {
    private BaseRefreshLayout refreshLayout;
    private RecyclerView rv;
    private OnUpAndDownListener onUpAndDownListener;
    private LtAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private View noItemView;

    public LTRecyclerView(Context context) {
        this(context, null);
    }

    public LTRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LTRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //创建rv
        rv = new MRecyclerView(context);
        //添加下拉刷新
        try {
            refreshLayout = thisRefreshLayout();
        } catch (Exception e) {
            throw new RuntimeException("请给下拉刷新的类留一个公有的空参构造(Please leave a public empty parameter structure for the RefreshLayout class)");
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
        this.addView((View) refreshLayout);

        //设置自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LTRecyclerView);
        //没有条目时的文字
        String noItemText = a.getString(R.styleable.LTRecyclerView_noItemText);
        if (!TextUtils.isEmpty(noItemText)) {
            setNoItemText(noItemText);
        }
        //没有条目时的布局或图片
        int noItemViewId = a.getResourceId(R.styleable.LTRecyclerView_noItemView, 0);
        if (noItemViewId != 0) {
            View noItemView = null;
            try {
                noItemView = View.inflate(context, noItemViewId, null);
            } catch (Exception e) {
                try {
                    noItemView = new ImageView(context);
                    ((ImageView) noItemView).setImageResource(noItemViewId);
                } catch (Exception e1) {
                }
            }
            setNoItemView(noItemView);
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
        rv.setLayoutManager(gridLayoutManager);//ViewGroup 管理者设置给 rv

        //添加上拉加载,这个是滚动的监听
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (refreshLayout != null && refreshLayout.isRefreshing())
                        return;
                    if (adapter == null)//如果没有适配器
                        return;
                    if (!adapter.getIsHaveData())//如果已标记为没数据
                        return;
                    if (gridLayoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                        //如果是最后一个条目,表示是上拉加载
                        if (onUpAndDownListener != null) {
                            onUpAndDownListener.up();
                        }
                    }
                }
            }
        });
    }

    /**
     * 类内部获取下拉刷新的View,可以继承并重写该方法来实现项目内不同的下拉刷新效果
     */
    protected BaseRefreshLayout thisRefreshLayout() throws InstantiationException, IllegalAccessException {
        return (BaseRefreshLayout) LtRecyclerViewManager.create().getRefreshLayoutClazz().newInstance();
    }

    /**
     * 设置一行展示多少列
     */
    public LTRecyclerView setSpanCount(int spanCount) {
        gridLayoutManager.setSpanCount(spanCount);
        return this;
    }

    /**
     * 添加上拉和下拉的回调接口
     *
     * @param onUpAndDownListener 回调接口
     */
    public LTRecyclerView setOnUpAndDownListener(OnUpAndDownListener onUpAndDownListener) {
        this.onUpAndDownListener = onUpAndDownListener;
        return this;
    }

    /**
     * 获取到自定义控件中包含的RecyclerView
     *
     * @return 自定义控件中包含的RecyclerView
     */
    public RecyclerView getRecyclerView() {
        return rv;
    }

    /**
     * 获得自定义控件中的RefreshLayout
     *
     * @return 自定义控件中的RefreshLayout
     */
    public BaseRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    /**
     * 设置RecyclerView的适配器
     *
     * @param adapter 继承自RecyclerView.Adapter的适配器
     */
    public LTRecyclerView setAdapter(LtAdapter adapter) {
        if (adapter == null)
            throw new RuntimeException("适配器为空(Adapter is null)");
        this.adapter = adapter;
        this.adapter.addOnNoItemListener(new OnNoItemListener() {
            @Override
            public void noItem() {
                //没有条目时隐藏rl,然后展示没条目时的布局
                if (noItemView != null) {
                    noItemView.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
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
        if (this.adapter.getLtItemCount() == 0 && this.adapter.getHeadListSize() == 0 && this.adapter.getTailListSize() == 0)
            rv.setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置没有条目时展示的View,默认是居中的
     */
    public LTRecyclerView setNoItemView(View view) {
        if (this.noItemView != null)
            this.removeView(this.noItemView);
        this.noItemView = view;
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        noItemView.setLayoutParams(lp);
        noItemView.setVisibility(adapter == null ? VISIBLE : INVISIBLE);
        try {
            this.addView(noItemView);
        } catch (IllegalStateException e) {
            throw new RuntimeException("指定的View已经有另一个父布局了(The specified View has another parent layout)");
        }
        return this;
    }

    public LTRecyclerView setNoItemText(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        setNoItemView(tv);
        return this;
    }

    public View getNoItemView() {
        return noItemView;
    }

    /**
     * 刷新rv的布局
     */
    public LTRecyclerView notifyDataSetChanged() {
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
     * 获取线性多列布局的管理者
     *
     * @return
     */
    public GridLayoutManager getLayoutManager() {
        return gridLayoutManager;
    }

    /**
     * 设置是否上拉加载
     */
    public LTRecyclerView setBottomRefresh(boolean b) {
        if (adapter != null)
            adapter.setRefresh(b);
        return this;
    }

    /**
     * 设置是否下拉刷新
     */
    public LTRecyclerView setTopRefresh(boolean b) {
        refreshLayout.setRefreshing(b);
        return this;
    }

    /**
     * 设置是否上拉加载(底部)和下拉刷新(顶部)
     */
    public LTRecyclerView setRefresh(boolean top, boolean bottom) {
        refreshLayout.setRefreshing(top);
        if (adapter != null)
            adapter.setRefresh(bottom);
        return this;
    }

    /**
     * 添加分割线
     */
    public LTRecyclerView addItemDecoration_line() {
        return addItemDecoration_line(2);
    }

    public LTRecyclerView addItemDecoration_line(int px) {
        return addItemDecoration_line(px, 0xffd5d5d5);
    }

    public LTRecyclerView addItemDecoration_line(int px, int color) {
        rv.addItemDecoration(new LtDivider(getContext(), LinearLayoutManager.HORIZONTAL, px, color));
        return this;
    }

    public LTRecyclerView addItemDecoration_drawable(int resId) {
        return addItemDecoration_drawable(ContextCompat.getDrawable(getContext(), resId));
    }

    public LTRecyclerView addItemDecoration_drawable(Drawable drawable) {
        rv.addItemDecoration(new LtDivider(getContext(), LinearLayoutManager.HORIZONTAL, drawable));
        return this;
    }
}
