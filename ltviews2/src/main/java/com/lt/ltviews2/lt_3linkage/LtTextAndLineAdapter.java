package com.lt.ltviews2.lt_3linkage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lt.ltviews2.R;
import com.lt.ltviews2.lt_listener.OnListener;
import com.lt.ltviews2.lt_listener.OnLt3LinkageListener;

import java.util.List;

/**
 * 创    建:  lt  2017/12/21--16:04
 * 作    用:
 * 注意事项:
 */

class LtTextAndLineAdapter extends RecyclerView.Adapter<LtTextAndLineAdapter.MViewHolder> {
    private List<String> list;
    private OnLt3LinkageListener onLt3LinkageListener;
    private OnListener onListener;

    public LtTextAndLineAdapter(List<String> list, OnLt3LinkageListener onLt3LinkageListener, OnListener onListener) {
        this.list = list;
        this.onLt3LinkageListener = onLt3LinkageListener;
        this.onListener = onListener;
    }

    @Override
    public LtTextAndLineAdapter.MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lt_item_text_and_line, parent, false));
    }

    @Override
    public void onBindViewHolder(LtTextAndLineAdapter.MViewHolder holder, final int position) {
        holder.tv.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLt3LinkageListener != null)
                    onLt3LinkageListener.onLt3LinkageListener(0, position);
            }
        });
    }

    public void refreshData() {
        if (onListener != null)
            onListener.on("");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
