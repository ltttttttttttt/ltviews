package com.lt.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lt.ltviews.lt_3linkage.Lt3LinkageManager;
import com.lt.ltviews.lt_doubletextview.LtDoubleTextView;
import com.lt.ltviews.lt_listener.OnLt3LinkageListener;
import com.lt.ltviews.lt_listener.OnRvItemClickListener;
import com.lt.ltviews.lt_listener.OnUpAndDownListener;
import com.lt.ltviews.lt_recyclerview.LTRecyclerView;
import com.lt.ltviews.lt_recyclerview.LtAdapter;
import com.lt.ltviews.lt_scrollimageview.LtPosition;
import com.lt.ltviews.lt_scrollimageview.LtScrollImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    private List<String> list1;
    private List<String> list2;
    private List<String> list3;
    String s1;
    String s2;
    TextView tv;
    LTRecyclerView rv;
    LtScrollImageView siv;
    LtDoubleTextView iv;
    int count = 100;

    private Lt3LinkageManager linkageManager;
    private LinearLayout ll1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll1 = (LinearLayout) findViewById(R.id.ll1);
        iv = (LtDoubleTextView) findViewById(R.id.iv);
        siv = (LtScrollImageView) findViewById(R.id.siv);
        rv = (LTRecyclerView) findViewById(R.id.rv);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(this);

        initRv();
        initSiv();
    }

    private void initSiv() {
        String[] srr = {"http://112.126.83.45:8066/Upload/2017/02/18/201702181702598486328.png",
                "http://112.126.83.45:8066/Upload/2017/02/18/201702181703078955078.png",
                "http://img.zcool.cn/community/01711b59426ca1a8012193a31e5398.gif",
                "http://pic4.nipic.com/20091217/3885730_124701000519_2.jpg"};
        siv.init(srr, 3000, 500, R.drawable.banner_xuanzhong, R.drawable.banner_weixuanzhong, R.drawable.ic_launcher_background)
                .setIvMargin(10)
                .setPosition(LtPosition.RIGHT)
                .setOnRvItemClickListener(new OnRvItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        LogUtil.i("lllttt", "MainActivity.onItemClick : " + position);
                    }
                });
    }

    private void initRv() {
//        rv.setSpanCount(10);
        final LtAdapter adapter = new LtAdapter(null) {
            @Override
            public RecyclerView.ViewHolder onLtCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(new TextView(getApplicationContext())) {
                };
            }

            @Override
            public int getLtItemCount() {
                return count;
            }

            @Override
            public void onLtBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText("" + position);
                holder.itemView.setBackgroundColor(Color.parseColor("#" + new Random().nextInt(9) + new Random().nextInt(9) + new Random().nextInt(9) + new Random().nextInt(9) + new Random().nextInt(9) + new Random().nextInt(9)));
            }
        };
        rv.setAdapter(adapter);
        rv.setOnUpAndDownListener(new OnUpAndDownListener() {
            @Override
            public void up() {
                LogUtil.i("lllttt", "MainActivity.up : ");
            }

            @Override
            public void down() {
                LogUtil.i("lllttt", "MainActivity.down : ");
                adapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv.setTopRefresh(false);
                    }
                }, 2000);
            }
        });
//        rv.getRefreshLayout().setEnabled(false);
        rv.setBottomRefresh(false);
//        adapter.setOnRvItemClickListener(new OnRvItemClickListener() {
//            @Override
//            public void onItemClick(View itemView, int position) {
//                LogUtil.i("lllttt", "MainActivity.onItemClick : " + position);
//            }
//        });
//        adapter.setOnRvItemLongClickListener(new OnRvItemLongClickListener() {
//            @Override
//            public void onItemLongClick(View itemView, int position) {
//                LogUtil.i("lllttt", "MainActivity.onItemLongClick : " + position);
//            }
//        });
//        rv.getRecyclerView().addItemDecoration(new LtDivider(this, LinearLayoutManager.HORIZONTAL));
    }

    @Override
    public void onClick(View view) {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list1.add("第一列" + i);
        }
        linkageManager = new Lt3LinkageManager(this, "请选择一个", list1, list2, list3)
                .setTextColor(R.color.colorAccent)
                .showLt3LinkageDialog(new OnLt3LinkageListener() {
                    @Override
                    public void onLt3LinkageListener(int type, int position) {
                        switch (type) {
                            case 1:
                                s1 = list1.get(position);
                                list2.clear();
                                for (int i = 0; i < 9; i++) {
                                    list2.add("第二列,position=" + position + "    " + i);
                                }
                                linkageManager.refresh();
                                break;
                            case 2:
                                s2 = list2.get(position);
                                list3.clear();
                                for (int i = 0; i < 9; i++) {
                                    list3.add("第三列,position=" + position + "    " + i);
                                }
                                linkageManager.refresh();
                                break;
                            case 3:
                                Toast.makeText(MainActivity.this, s1 + s2 + list3.get(position), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
    }

    public void onClicka(View view) {
        Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
    }
}
