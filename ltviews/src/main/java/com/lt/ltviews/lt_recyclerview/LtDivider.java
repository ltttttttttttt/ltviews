package com.lt.ltviews.lt_recyclerview;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * rv的分割线类
 */
public class LtDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int orientation;//方向,-1为gridview
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private int spanCount;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param rv
     */
    public LtDivider(@NonNull RecyclerView rv) {
        final TypedArray a = rv.getContext().obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        orientation = rv.getLayoutManager() instanceof LinearLayoutManager ? ((LinearLayoutManager) rv.getLayoutManager()).getOrientation() : -1;
    }

    /**
     * 自定义分割线
     *
     * @param rv
     * @param drawableId 分割线图片
     */
    public LtDivider(@NonNull RecyclerView rv, int drawableId) {
        this(rv, ContextCompat.getDrawable(rv.getContext(), drawableId));
    }

    public LtDivider(@NonNull RecyclerView rv, @NonNull Drawable drawable) {
        this(rv);
        mDivider = drawable;
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param rv
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public LtDivider(@NonNull RecyclerView rv, int dividerHeight, int dividerColor) {
        this(rv);
        mDividerHeight = dividerHeight;
        mDivider = new GradientDrawable();
        ((GradientDrawable) mDivider).setColor(dividerColor);
    }


    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        if (parent.getLayoutManager().getPosition(view) == childCount - 1)// 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0, 0, 0);
        } else if (isLastColum(parent, spanCount, view))// 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight,
                    mDividerHeight);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastColum(RecyclerView parent, int spanCount, View view) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int pos = layoutManager.getPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            // 如果是有head,则去掉head,如果是最后一列，则不需要绘制右边
            if (parent.getAdapter() instanceof LtAdapter) {
                int headSize = ((LtAdapter) parent.getAdapter()).getHeadListSize();
                if ((pos - headSize + 1) % spanCount == 0) {
                    return true;
                }
            } else if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        }
        return false;
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (spanCount > 1) {
            drawVertical(c, parent);
            drawHorizontal(c, parent);
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            drawHorizontal(c, parent);
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            drawVertical(c, parent);
        }
    }

    //绘制横向 item_person_select 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + /*mDivider.getIntrinsicWidth()*/mDividerHeight;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + /*mDivider.getIntrinsicHeight()*/mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    //绘制纵向 item_person_select 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        if (parent.getAdapter() instanceof LtAdapter)
            childCount--;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + /*mDivider.getIntrinsicWidth()*/mDividerHeight;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }
}