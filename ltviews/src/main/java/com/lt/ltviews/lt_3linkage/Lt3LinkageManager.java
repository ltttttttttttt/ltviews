package com.lt.ltviews.lt_3linkage;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lt.ltviews.R;
import com.lt.ltviews.lt_listener.OnListener;
import com.lt.ltviews.lt_listener.OnLt3LinkageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 创    建:  lt  2018/1/3--10:42
 * 作    用:  三级联动管理者
 * 注意事项:
 */

public class Lt3LinkageManager {
    private Activity activity;
    private String title;
    private List<String> contextList1;
    private List<String> contextList2;
    private List<String> contextList3;
    private ImageView iv_close;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private View view1;
    private View view2;
    private View view3;
    private RecyclerView rv;
    private int black_252525 = 0xff252525;
    private int color_6fc777;
    private List<String> list;
    private AlertDialog dialog;
    private LtTextAndLineAdapter textAndLineAdapter;
    private OnLt3LinkageListener onLt3LinkageListener;

    public Lt3LinkageManager(Activity activity, String title, final List<String> contextList1, final List<String> contextList2, final List<String> contextList3) {
        this.activity = activity;
        this.title = title;
        this.contextList1 = contextList1;
        this.contextList2 = contextList2;
        this.contextList3 = contextList3;
        color_6fc777 = activity.getResources().getColor(R.color.colorPrimary);

        //通过 Builder 创建对话框
        dialog = new AlertDialog.Builder(activity, R.style.ltCustomDialogStyle).create();
        dialog.show();
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setContentView(R.layout.lt_dialog_address);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setWindowAnimations(R.style.ltAnimBottom);
        window.setGravity(Gravity.BOTTOM);

        ((TextView) window.findViewById(R.id.tv_title)).setText(title);
        rv = (RecyclerView) window.findViewById(R.id.rv);
        view1 = (View) window.findViewById(R.id.view1);
        view2 = (View) window.findViewById(R.id.view2);
        view3 = (View) window.findViewById(R.id.view3);
        tv1 = (TextView) window.findViewById(R.id.tv1);
        tv2 = (TextView) window.findViewById(R.id.tv2);
        tv3 = (TextView) window.findViewById(R.id.tv3);
        iv_close = (ImageView) window.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        list = new ArrayList<>();
        list.addAll(contextList1);
        textAndLineAdapter = new LtTextAndLineAdapter(list, new OnLt3LinkageListener() {
            @Override
            public void onLt3LinkageListener(int type, int position) {
                if (view2.getVisibility() == View.VISIBLE) {
                    //选择完了第二层
                    tv2.setText(contextList2.get(position));
                    tv2.setTextColor(black_252525);
                    view2.setVisibility(View.INVISIBLE);
                    tv3.setText("请选择");
                    tv3.setVisibility(View.VISIBLE);
                    tv3.setTextColor(color_6fc777);
                    view3.setVisibility(View.VISIBLE);
                    list.clear();
                    list.addAll(contextList3);
                    if (onLt3LinkageListener != null)
                        onLt3LinkageListener.onLt3LinkageListener(2, position);
                    textAndLineAdapter.notifyDataSetChanged();
                } else if (view3.getVisibility() == View.VISIBLE) {
                    //选择完了第三层
                    if (onLt3LinkageListener != null)
                        onLt3LinkageListener.onLt3LinkageListener(3, position);
                    dialog.dismiss();
                } else {
                    //选择完了第一层
                    tv1.setText(contextList1.get(position));
                    tv1.setTextColor(black_252525);
                    view1.setVisibility(View.INVISIBLE);
                    tv2.setText("请选择");
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setTextColor(color_6fc777);
                    view2.setVisibility(View.VISIBLE);
                    list.clear();
                    list.addAll(contextList2);
                    if (onLt3LinkageListener != null)
                        onLt3LinkageListener.onLt3LinkageListener(1, position);
                    textAndLineAdapter.notifyDataSetChanged();
                }
            }
        }, new OnListener() {
            @Override
            public void on(String s) {
                if (view2.getVisibility() == View.VISIBLE) {
                    //是第二层
                    list.clear();
                    list.addAll(contextList2);
                } else if (view3.getVisibility() == View.VISIBLE) {
                    //是第三层
                    list.clear();
                    list.addAll(contextList3);
                }
            }
        });
        rv.setAdapter(textAndLineAdapter);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.INVISIBLE);
                view3.setVisibility(View.INVISIBLE);
                tv1.setText("请选择");
                tv1.setTextColor(color_6fc777);
                tv2.setVisibility(View.INVISIBLE);
                tv3.setVisibility(View.INVISIBLE);
                list.clear();
                list.addAll(contextList1);
                textAndLineAdapter.notifyDataSetChanged();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.INVISIBLE);
                tv2.setText("请选择");
                tv2.setTextColor(color_6fc777);
                tv3.setVisibility(View.INVISIBLE);
                list.clear();
                list.addAll(contextList2);
                textAndLineAdapter.notifyDataSetChanged();
            }
        });
    }

    public Lt3LinkageManager showLt3LinkageDialog(OnLt3LinkageListener onLt3LinkageListener) {
        if (dialog != null && !dialog.isShowing())
            dialog.show();
        this.onLt3LinkageListener = onLt3LinkageListener;
        return this;
    }

    public Lt3LinkageManager setTextColor(int resId) {
        this.color_6fc777 = activity.getResources().getColor(resId);
        tv1.setTextColor(color_6fc777);
        view1.setBackgroundColor(color_6fc777);
        view2.setBackgroundColor(color_6fc777);
        view3.setBackgroundColor(color_6fc777);
        return this;
    }

    public void refresh() {
        textAndLineAdapter.refreshData();
    }
}
