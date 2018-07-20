package com.lt.ltviews.lt_recyclerview;

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

public abstract class LtAdapter extends RecyclerView.Adapter {
    /**
     * 上拉刷新的View
     */
    private View view = null;//上拉的View
    private final boolean noDataIsLoad;//没数据时是否刷新
    private View ll1;//有数据时的view
    private View ll2;//没数据时的view
    private List<OnNoItemListener> onNoItemListenerList;
    private boolean noData = true;//true表示没数据
    private List<View> headList;//头部的条目集合
    private List<View> tailList;//尾部的条目集合
    private OnRvItemClickListener onRvItemClickListener;
    private OnRvItemLongClickListener onRvItemLongClickListener;

    /**
     * 传入null会使用默认的上拉View
     *
     * @param view
     */
    public LtAdapter(View view) {
        if (view == null)
            view = View.inflate(LtRecyclerViewManager.create().getContext(), LtRecyclerViewManager.create().getUpLayoutId(), null);
        this.view = view;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        ll1 = view.findViewById(R.id.ll1);
        ll2 = view.findViewById(R.id.ll2);
        noDataIsLoad = LtRecyclerViewManager.create().isNoDataIsLoad();
    }

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder onLtCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //顶部和底部返回特定的ViewHolder
        if (viewType == 12345679) return new RecyclerView.ViewHolder(view) {
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
     *
     * @param position
     * @return
     */
    public int getLtItemViewType(int position) {
        return 0;
    }

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
            return 12345679;//表示是底部的上拉加载布局
        } else return getLtItemViewType(position - (headList == null ? 0 : headList.size()));
    }

    /**
     * 返回显示的条目的数量
     *
     * @return
     */
    public abstract int getLtItemCount();

    @Override
    public int getItemCount() {
        //如果调用了一次无数据,下次有数据的时候就调用有数据,如果之前没调过无数据,就不相应有数据
        if (!noData && (onNoItemListenerList != null && onNoItemListenerList.size() != 0) && getLtItemCount() == 0 && (headList == null || headList.size() == 0) && (tailList == null || tailList.size() == 0)) {
            for (OnNoItemListener onNoItemListener : onNoItemListenerList) {
                onNoItemListener.noItem();
            }
            noData = true;
            //如果没数据,但是变成有数据了,就调用有数据的回调,并修改为有数据
        } else if (noData && (onNoItemListenerList != null && onNoItemListenerList.size() != 0) && (getLtItemCount() != 0 || (headList != null && headList.size() != 0) || (tailList != null && tailList.size() != 0))) {
            for (OnNoItemListener onNoItemListener : onNoItemListenerList) {
                onNoItemListener.haveItem();
            }
            noData = false;
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
    public LtAdapter setRefresh(boolean b) {
        if (ll1 == null || ll2 == null) {
            notifyDataSetChanged();
            return this;
        }
        ll1.setVisibility(b ? View.VISIBLE : View.GONE);
        ll2.setVisibility(!b ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
        return this;
    }

    /**
     * 用于设置数据
     */
    public abstract void onLtBindViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //条目长按事件
        if (onRvItemLongClickListener != null)
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onRvItemLongClickListener == null)
                        return false;
                    onRvItemLongClickListener.onItemLongClick(view, position);
                    return true;
                }
            });
        //条目点击事件
        if (onRvItemClickListener != null)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRvItemClickListener == null)
                        return;
                    onRvItemClickListener.onItemClick(view, position);
                }
            });
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
            onLtBindViewHolder(holder, position - headList.size());
        else
            onLtBindViewHolder(holder, position);
    }

    /**
     * 设置没有数据时的回调,调用后,则不会自动显示和隐藏没条目的view
     * 建议使用addOnNoItemListener
     */
    @Deprecated
    public LtAdapter setOnNoItemListener(OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        else
            onNoItemListenerList.clear();
        onNoItemListenerList.add(onNoItemListener);
        return this;
    }

    /**
     * 添加没有数据时的回调
     *
     * @param onNoItemListener 回调
     */
    public LtAdapter addOnNoItemListener(OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        onNoItemListenerList.add(onNoItemListener);
        return this;
    }

    public LtAdapter addOnNoItemListener(int position, OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null)
            onNoItemListenerList = new ArrayList<>();
        onNoItemListenerList.add(position, onNoItemListener);
        return this;
    }

    public LtAdapter removeOnNoItemListener(OnNoItemListener onNoItemListener) {
        if (onNoItemListenerList == null || onNoItemListenerList.size() == 0)
            return this;
        try {
            onNoItemListenerList.remove(onNoItemListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 添加头部的布局
     */
    public LtAdapter addHeadView(View view) {
        if (headList == null) {
            headList = new ArrayList<>();
        }
        headList.add(view);
        return this;
    }

    /**
     * 添加头部的布局
     */
    public LtAdapter addHeadView(View view, int position) {
        if (headList == null) {
            headList = new ArrayList<>();
        }
        headList.add(position, view);
        return this;
    }

    /**
     * 删除头部的布局
     */
    public LtAdapter removeHeadView(View view) {
        if (headList != null) {
            headList.remove(view);
        }
        return this;
    }

    /**
     * 删除头部的布局
     */
    public LtAdapter removeHeadView(int position) {
        if (headList != null) {
            headList.remove(position);
        }
        return this;
    }

    /**
     * @return 获取头条目的个数
     */
    public int getHeadListSize() {
        if (headList == null)
            return 0;
        return headList.size();
    }

    /**
     * @return 获取尾条目的个数
     */
    public int getTailListSize() {
        if (tailList == null)
            return 0;
        return tailList.size();
    }

    /**
     * 添加底部的布局
     */
    public LtAdapter addTailView(View view) {
        if (tailList == null) {
            tailList = new ArrayList<>();
        }
        tailList.add(view);
        return this;
    }

    /**
     * 添加底部的布局
     */
    public LtAdapter addTailView(View view, int position) {
        if (tailList == null) {
            tailList = new ArrayList<>();
        }
        tailList.add(position, view);
        return this;
    }

    /**
     * 删除底部的布局
     */
    public LtAdapter removeTailView(View view) {
        if (tailList != null) {
            tailList.remove(view);
        }
        return this;
    }

    /**
     * 删除底部的布局
     */
    public LtAdapter removeTailView(int position) {
        if (tailList != null) {
            tailList.remove(position);
        }
        return this;
    }

    /**
     * 获取刷新的View
     */
    public View getRefreshView() {
        return this.view;
    }

    /**
     * 适用于GridView,使条目跨列
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        final GridLayoutManager gridManager = ((GridLayoutManager) recyclerView.getLayoutManager());
        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = getItemViewType(position);
                return ((itemViewType == 12345679)
                        || (itemViewType >= 12345500 && itemViewType < 12345600)
                        || (itemViewType >= 12345600 && itemViewType < 12345700))
                        ? gridManager.getSpanCount() : 1;
            }
        });
    }

    /**
     * 设置条目的点击事件监听
     */
    public LtAdapter setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
        notifyDataSetChanged();
        return this;
    }

    /**
     * 设置条目的长按事件监听
     */
    public LtAdapter setOnRvItemLongClickListener(OnRvItemLongClickListener onRvItemLongClickListener) {
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
