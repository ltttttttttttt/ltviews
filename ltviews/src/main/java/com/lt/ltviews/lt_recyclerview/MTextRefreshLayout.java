package com.lt.ltviews.lt_recyclerview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.lt.ltviews.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 创    建:  lt  2018/5/23--18:42
 * 作    用:  文字的下拉刷新
 * 注意事项:
 */

public class MTextRefreshLayout extends LtRefreshLayout {

    TextView tv;
    TextView tvDate;
    ImageView iv;
    ObjectAnimator oa;
    String date;
    SimpleDateFormat sdf;

    public MTextRefreshLayout(Context context) {
        this(context, null);
    }

    public MTextRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTextRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sdf = new SimpleDateFormat("M-d H:m");
        date = getDate();
    }

    @Override
    protected void onStatus(int status) {
        switch (status) {
            case REFRESH_DOWN:
                //下拉中
                tv.setText("下拉可以刷新");
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.lt_arrow);
                //设置动画时间
                oa = ObjectAnimator.ofFloat(iv, "rotation", iv.getRotation(), 0)
                        .setDuration(animationTime);
                oa.setRepeatCount(0);//设置动画执行的次数
                oa.start();//开始动画
                break;
            case REFRESH_RELEASE:
                //松开刷新
                tv.setText("释放立即刷新");
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.lt_arrow);
                oa = ObjectAnimator.ofFloat(iv, "rotation", iv.getRotation(), 180)
                        .setDuration(animationTime);//设置动画时间
                oa.setRepeatCount(0);//设置动画执行的次数
                oa.start();//开始动画
                break;
            case REFRESHING:
                //刷新中
                tv.setText("正在刷新...");
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.lt_loading);
                oa = ObjectAnimator.ofFloat(iv, "rotation", 0, 360)
                        .setDuration(animationTime << 2);//设置动画时间
                oa.setInterpolator(new LinearInterpolator());
                oa.setRepeatCount(ObjectAnimator.INFINITE);//设置动画执行的次数,这个是无限
                oa.start();//开始动画
                break;
            case REFRESH_FINISH:
                //刷新完成
                date = setDate();
                tv.setText("刷新完成");
                tvDate.setText("上次更新: " + date);
                iv.clearAnimation();
                oa.cancel();
                iv.setVisibility(View.INVISIBLE);
                iv.setImageResource(R.drawable.lt_arrow);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onProgress(float y) {

    }

    @Override
    protected View getRefreshView() {
        View view = View.inflate(getContext(), R.layout.lt_refresh_view, null);
        tv = (TextView) view.findViewById(R.id.tv_lt_refresh);
        tvDate = (TextView) view.findViewById(R.id.tv_lt_date);
        iv = (ImageView) view.findViewById(R.id.iv_lt_refresh);
        tvDate.setText("上次更新: " + date);
        return view;
    }

    String getDate() {
        SharedPreferences preference = getContext().getSharedPreferences("lt_rv",
                Context.MODE_PRIVATE);
        return preference.getString("lt_date", "");
    }

    String setDate() {
        String value = sdf.format(new Date());
        SharedPreferences preference = getContext().getSharedPreferences("lt_rv",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("lt_date", value);
        editor.commit();
        return value;
    }
}
