package com.lt.ltviews.lt_doubletextview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lt.ltviews.R;

/**
 * 创    建:  lt  2018/1/5--11:53
 * 作    用:  常用的左右两边各有一个textview
 * 注意事项:
 */

public class LtDoubleTextView extends FrameLayout {
    private float textSize;
    private int margin;
    private int leftTextColor;
    private int rightTextColor;
    private TextView leftTv;
    private TextView rightTv;

    public LtDoubleTextView(Context context) {
        this(context, null);
    }

    public LtDoubleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LtDoubleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources resources = getResources();
        leftTextColor = resources.getColor(R.color.ltLeftTextColor);
        rightTextColor = resources.getColor(R.color.ltRightTextColor);
        textSize = resources.getDimension(R.dimen.ltDoubleTextSize);
        margin = 30;

        leftTv = new TextView(context);
        rightTv = new TextView(context);
        LayoutParams leftLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams rightLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        leftLp.leftMargin = margin;
        leftLp.gravity = Gravity.CENTER_VERTICAL;
        leftTv.setTextColor(leftTextColor);
        leftTv.setTextSize(textSize);

        rightLp.rightMargin = margin;
        rightLp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        rightTv.setTextColor(rightTextColor);
        rightTv.setTextSize(textSize);

        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.LtDoubleTextView);
            leftTv.setText(t.getString(R.styleable.LtDoubleTextView_leftText));
            rightTv.setText(t.getString(R.styleable.LtDoubleTextView_rightText));
            leftTv.setTextColor(t.getColor(R.styleable.LtDoubleTextView_leftTextColor, leftTextColor));
            rightTv.setTextColor(t.getColor(R.styleable.LtDoubleTextView_rightTextColor, rightTextColor));
            leftTv.setTextSize(t.getDimension(R.styleable.LtDoubleTextView_leftTextSize, textSize) / context.getResources().getDisplayMetrics().density);
            rightTv.setTextSize(t.getDimension(R.styleable.LtDoubleTextView_rightTextSize, textSize) / context.getResources().getDisplayMetrics().density);
            t.recycle();
        }
        leftTv.setLayoutParams(leftLp);
        rightTv.setLayoutParams(rightLp);
        addView(leftTv);
        addView(rightTv);
    }

    public TextView getLeftTV() {
        return leftTv;
    }

    public TextView getRightTv() {
        return rightTv;
    }

    public String getLeftText() {
        return leftTv.getText().toString();
    }

    public String getRightText() {
        return rightTv.getText().toString();
    }

    public LtDoubleTextView setLeftText(String s) {
        leftTv.setText(s);
        return this;
    }

    public LtDoubleTextView setRightText(String s) {
        rightTv.setText(s);
        return this;
    }

    public LtDoubleTextView setLeftTextColor(int color) {
        leftTextColor = color;
        leftTv.setTextColor(leftTextColor);
        return this;
    }

    public LtDoubleTextView setRightTextColor(int color) {
        rightTextColor = color;
        rightTv.setTextColor(rightTextColor);
        return this;
    }

    public LtDoubleTextView setTextSize(float size) {
        textSize = size;
        leftTv.setTextSize(textSize);
        rightTv.setTextSize(textSize);
        return this;
    }
}