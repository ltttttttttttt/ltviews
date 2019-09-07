package com.lt.ltviews.lt_recyclerview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lt.ltviews.R;
import com.lt.ltviews.lt_listener.OnNoItemListener;
import com.lt.ltviews.lt_listener.OnRvItemClickListener;
import com.lt.ltviews.lt_listener.OnRvItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 所在包名:  com.lt.ltrecyclerview
 * 创建日期:  2017/4/28--10:38
 * 作   用:   适配器
 * 使用方法:
 * 注意事项:
 */

public abstract class LtAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    /**
     * 上拉刷新的View
     */
    private View view = null;//上拉的View
    private final boolean noDataIsLoad;//没数据时是否刷新
    private View ll1;//有数据时的view
    private View ll2;//没数据时的view
    private List<OnNoItemListener> onNoItemListenerList;
    private List<View> headList;//头部的条目集合
    private List<View> tailList;//尾部的条目集合
    private OnRvItemClickListener onRvItemClickListener;
    private OnRvItemLongClickListener onRvItemLongClickListener;

    public LtAdapter() {
        this(null);
    }

    /**
     * 传入null会使用默认的上拉View
     */
    public LtAdapter(@Nullable View view) {
        if (view == null)
            view = View.inflate(LtRecyclerViewManager.getInstance().getContext(), LtRecyclerViewManager.getInstance().getUpLayoutId(), null);
        this.view = view;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        ll1 = view.findViewById(R.id.ll1);
        ll2 = view.findViewById(R.id.ll2);
        noDataIsLoad = LtRecyclerViewManager.getInstance().isNoDataIsLoad();
    }

    /**
     * 创建ViewHolder
     */
    public abstract @NonNull
    VH onLtCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * @deprecated 一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtCreateViewHolder}
     */
    @Deprecated
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //顶部和底部返回特定的ViewHolder
        if (viewType == 12345701) return new RecyclerView.ViewHolder(view) {
        };
        else if (viewType >= 12345500 && viewType < 12345600)
            return new RecyclerView.ViewHolder(headList.get(viewType - 12345500)) {
            };
        else if (viewType >= 12345600 && viewType < 12345700)
            return new RecyclerView.ViewHolder(tailList.get(viewType - 12345600)) {
            };
        else return onLtCreateViewHolder(parent, viewType);
    }

    /**
     * 获取Type
     */
    public int getLtItemViewType(int position) {
        return 0;
    }

    /**
     * @deprecated 一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemViewType}
     */
    @Deprecated
    @Override
    public int getItemViewType(int position) {
        //给顶部和底部的布局加上特定的type
        if (headList != null) {
            if (position <= headList.size() - 1) {
                return 12345500 + position;//表示头部
            }
        }
        if (tailList != null) {
            if ((position >= getLtItemCount() + (headList == null ? 0 : headList.size())) && position < getItemCount() - 1) {
                return 12345600 + (position - getLtItemCount() - (headList == null ? 0 : headList.size()));//表示尾部
            }
        }
        if (position == getItemCount() - 1) {
            return 12345701;//表示是底部的上拉加载布局
        } else return getLtItemViewType(position - (headList == null ? 0 : headList.size()));
    }

    /**
     * 返回显示的条目的数量
     */
    public abstract int getLtItemCount();

    /**
     * @deprecated 一般情况下请勿重写该方法, 请复写:{@link LtAdapter#getLtItemCount}
     */
    @Deprecated
    @Override
    public int getItemCount() {
        //如果调用了一次无数据,下次有数据的时候就调用有数据,如果之前没调过无数据,就不相应有数据
        if ((onNoItemListenerList != null && onNoItemListenerList.size() != 0) && getLtItemCount() == 0 && (headList == null || headList.size() == 0) && (tailList == null || tailList.size() == 0)) {
            for (OnNoItemListener onNoItemListener : onNoItemListenerList) {
                onNoItemListener.noItem();
            }
            //如果没数据,但是变成有数据了,就调用有数据的回调,并修改为有数据
        } else if ((onNoItemListenerList != null && onNoItemListenerList.size() != 0) && (getLtItemCount() != 0 || (headList != null && headList.size() != 0) || (tailList != null && tailList.size() != 0))) {
            for (OnNoItemListener onNoItemListener : onNoItemListenerList) {
                onNoItemListener.haveItem();
            }
        }
        //加上多的顶部和底部的条目
        int plus = 1;
        if (headList != null) {
            plus += headList.size();
        }
        if (tailList != null) {
            plus += tailList.size();
        }
        return getLtItemCount() + plus;
    }

    /**
     * 设置是否是刷新,没有更多数据的时候传入false,否则true
     */
    public @NonNull
    LtAdapter setRefresh(boolean b) {
        if (ll1 == null || ll2 == null) {
            return this;
        }
        ll1.setVisibility(b ? View.VISIBLE : View.GONE);
        ll2.setVisibility(!b ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 用于设置数据
     */
    public abstract void onLtBindViewHolder(@NonNull VH holder, int position);

    /**
     * @deprecated 一般情况下请勿重写该方法, 请复写:{@link LtAdapter#onLtBindViewHolder}
     */
    @Deprecated
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //条目长按事件
        if (onRvItemLongClickListener != null)
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onRvItemLongClickListener == null) {
                        return false;
                    }
                    onRvItemLongClickListener.onItemLongClick(view, position);
                    return true;
                }
            });
        //条目点击事件
        if (onRvItemClickListener != null)
            if (!holder.itemView.hasOnClickListeners()) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onRvItemClickListener == null)
                            return;
                        onRvItemClickListener.onItemClick(view, holder.getAdapterPosition());
                    }
                });
            }
        //给不是头部,不是尾部,不是上拉的布局提供方法,并减去头部的条目数
        if (headList != null) {
            if (position <= headList.size() - 1) {
                return;
            }
        }
        if (tailList != null) {
            if (position >= getLtItemCount() + (headList == null ? 0 : headList.size()) && position < getItemCount() - 1) {
                return;
            }
        }
        if (position == getItemCount() - 1) {
            return;
        } else if (headList != null)
            onLtBindViewHolder((VH) holder, position - headList.size());
        else
            onLtBindViewHolder((VH) holder, position);
    }

    /**
     * @deprecated 设置没有数据时的回调, 调用后, 则不会自动显示和隐藏没条目的view,请使用{@link LtAdapter#addOnNoItemListener}
     */
    @Deprecated
    public @NonNull
    LtAdapter setOnNoItemListener(@Nullable OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        else
            onNoItemListenerList.clear();
        onNoItemListenerList.add(onNoItemListener);
        return this;
    }

    /**
     * 添加没有数据时的回调
     */
    public @NonNull
    LtAdapter addOnNoItemListener(@NonNull OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        onNoItemListenerList.add(onNoItemListener);
        return this;
    }

    public @NonNull
    LtAdapter addOnNoItemListener(int position, @NonNull OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        onNoItemListenerList.add(position, onNoItemListener);
        return this;
    }

    /**
     * 移除没有数据时的回调
     */
    public @NonNull
    LtAdapter removeOnNoItemListener(@NonNull OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null || onNoItemListenerList.size() == 0)
            return this;
        onNoItemListenerList.remove(onNoItemListener);
        return this;
    }

    /**
     * 获取没有数据时的回调
     */
    public @Nullable
    List<OnNoItemListener> getOnNoItemListenerList() {
        return onNoItemListenerList;
    }

    /**
     * 添加头部的布局
     */
    public @NonNull
    LtAdapter addHeadView(@NonNull View view) {
        if (headList == null) {
            headList = new ArrayList<>();
        }
        headList.add(view);
        return this;
    }

    public @NonNull
    LtAdapter addHeadView(@NonNull View view, int position) {
        if (headList == null) {
            headList = new ArrayList<>();
        }
        headList.add(position, view);
        return this;
    }

    /**
     * 删除头部的布局
     */
    public @NonNull
    LtAdapter removeHeadView(@NonNull View view) {
        if (headList != null) {
            headList.remove(view);
        }
        return this;
    }

    public @NonNull
    LtAdapter removeHeadView(int position) {
        if (headList != null) {
            headList.remove(position);
        }
        return this;
    }

    /**
     * 获取头条目的个数
     */
    public int getHeadListSize() {
        return headList == null ? 0 : headList.size();
    }

    /**
     * 获取尾条目的个数
     */
    public int getTailListSize() {
        return tailList == null ? 0 : tailList.size();
    }

    /**
     * 添加底部的布局
     */
    public @NonNull
    LtAdapter addTailView(@NonNull View view) {
        if (tailList == null) {
            tailList = new ArrayList<>();
        }
        tailList.add(view);
        return this;
    }

    public @NonNull
    LtAdapter addTailView(@NonNull View view, int position) {
        if (tailList == null) {
            tailList = new ArrayList<>();
        }
        tailList.add(position, view);
        return this;
    }

    /**
     * 删除底部的布局
     */
    public @NonNull
    LtAdapter removeTailView(@NonNull View view) {
        if (tailList != null) {
            tailList.remove(view);
        }
        return this;
    }

    public @NonNull
    LtAdapter removeTailView(int position) {
        if (tailList != null) {
            tailList.remove(position);
        }
        return this;
    }

    /**
     * 获取头布局列表
     */
    public @Nullable
    List<View> getHeadList() {
        return headList;
    }

    /**
     * 获取尾布局列表
     */
    public @Nullable
    List<View> getTailList() {
        return tailList;
    }

    /**
     * 获取上拉刷新的View
     */
    public @Nullable
    View getRefreshView() {
        return this.view;
    }

    /**
     * 适用于GridView,使条目跨列
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (!(recyclerView.getLayoutManager() instanceof GridLayoutManager))
            return;
        final GridLayoutManager gridManager = ((GridLayoutManager) recyclerView.getLayoutManager());
        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = getItemViewType(position);
                return ((itemViewType == 12345701)
                        || (itemViewType >= 12345500 && itemViewType < 12345600)
                        || (itemViewType >= 12345600 && itemViewType < 12345700))
                        ? gridManager.getSpanCount() : 1;
            }
        });
    }

    /**
     * 设置条目的点击事件监听,请注意不要同时设置此回调和给holder.itemView设置长按事件
     */
    public @NonNull
    LtAdapter setOnRvItemClickListener(@Nullable OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 设置条目的长按事件监听,请注意不要同时设置此回调和给holder.itemView设置点击事件
     */
    public @NonNull
    LtAdapter setOnRvItemLongClickListener(@Nullable OnRvItemLongClickListener onRvItemLongClickListener) {
        this.onRvItemLongClickListener = onRvItemLongClickListener;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 获取是否上拉没数据状态,false表示没更多数据
     */
    public boolean getIsHaveData() {//如果ll2为null或已经无数据,就可以返回是否要加载数据
        return !(ll2 == null || ll2.getVisibility() == View.VISIBLE) || noDataIsLoad;
    }
}