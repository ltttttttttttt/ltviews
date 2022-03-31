package com.lt.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener;
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener;
import com.lt.ltviewsx.lt_listener.OnRvItemLongClickListener;
import com.lt.ltviewsx.lt_listener.OnUpAndDownListener;
import com.lt.ltviewsx.lt_recyclerview.LTRecyclerView;
import com.lt.ltviewsx.lt_recyclerview.LtAdapter;
import com.lt.ltviewsx.lt_scrollimageview.LtAdImageView;
import com.lt.ltviewsx.lt_scrollimageview.LtScrollImageView;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends Activity implements View.OnClickListener {

    private List<String> list1;
    private List<String> list2;
    private List<String> list3;
    String s1;
    String s2;
    TextView tv;
    LTRecyclerView rv;
    LtScrollImageView siv;
    LtAdImageView adIv;
    int count = 100;

    //private Lt3LinkageManager linkageManager;
    private LinearLayout ll1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, Main3Activity.class));

        ll1 = (LinearLayout) findViewById(R.id.ll1);
//        iv = (LtDoubleTextView) findViewById(R.id.iv);
        siv = (LtScrollImageView) findViewById(R.id.siv);
//        view = (LTRecyclerView) findViewById(R.id.view);
        tv = (TextView) findViewById(R.id.tv);
        adIv = (LtAdImageView) findViewById(R.id.adIv);
        tv.setOnClickListener(this);

        initRv();
        initSiv();
        initAd();
    }

    private void initAd() {
        List<String> srr = new ArrayList<String>();
        srr.add("http://img.zcool.cn/community/01711b59426ca1a8012193a31e5398.gif");
        srr.add("https://img1.baidu.com/it/u=1728076300,3153537570&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=375");
        srr.add("https://img1.baidu.com/it/u=1407750889,3441968730&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=799");
        srr.add("https://img1.baidu.com/it/u=4020015307,4170910140&fm=253&fmt=auto&app=138&f=JPEG?w=499&h=312");
        srr.add("https://img1.baidu.com/it/u=3796593454,4087161325&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500");
        adIv.initData(srr, 3000, new OnImageViewLoadUrlListener() {
            @Override
            public void onLoad(@NonNull ImageView iv, String url) {
                Glide.with(MainActivity.this)
                        .load(url)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(iv);
            }
        }, new OnRvItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.e("lllttt", itemView.toString() + "   " + position);
            }
        }, new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer integer) {
                //Log.e("lllttt", integer.toString());
                return Unit.INSTANCE;
            }
        });
    }

    private void initSiv() {
        List<String> srr = new ArrayList<String>();
        srr.add("http://img.zcool.cn/community/01711b59426ca1a8012193a31e5398.gif");
        srr.add("https://img1.baidu.com/it/u=1728076300,3153537570&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=375");
        srr.add("https://img1.baidu.com/it/u=1407750889,3441968730&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=799");
        srr.add("https://img1.baidu.com/it/u=4020015307,4170910140&fm=253&fmt=auto&app=138&f=JPEG?w=499&h=312");
        srr.add("https://img1.baidu.com/it/u=3796593454,4087161325&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500");
        siv.setIvMargin(10)
                .init(srr, 3000, R.drawable.banner_xuanzhong, R.drawable.banner_weixuanzhong, new OnImageViewLoadUrlListener() {
                    @Override
                    public void onLoad(ImageView iv, String url) {
                        Glide.with(MainActivity.this)
                                .load(url)
                                .error(R.drawable.ic_launcher_background)
                                .centerCrop()
                                .into(iv);
                    }
                }, new OnRvItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int position) {
                        LogUtil.e("lllttt", "MainActivity.onItemClick : " + position);
                    }
                }, new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        LogUtil.e("lllttt", "MainActivity.onItemChangeListener : " + integer);
                        return Unit.INSTANCE;
                    }
                })
                /*.setPosition(LtPosition.CENTER_BOTTOM_OUT)*/;
    }

    private void initRv() {
        rv = (LTRecyclerView) findViewById(R.id.rv);
        //创建一个适配器
        final ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            list.add("" + i);
//        }
        final LtAdapter adapter = new TextAdapter(this, list);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 100; i++) {
//                    list.add("" + i);
//                }
//                adapter.notifyDataSetChanged();
//                LogUtil.e("lllttt",rv.getRecyclerView().getHeight()+"");
//            }
//        }, 2000);
//        final LtAdapter adapter = new LtAdapter(null) {
//
//            @Override
//            public RecyclerView.ViewHolder onLtCreateViewHolder(ViewGroup parent, int viewType) {
//                return new CountryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_test1, parent, false));
//            }
//
//            @Override
//            public int getLtItemCount() {
//                //适配器展示多少条数据
//                return 99999;
//            }
//
//            @Override
//            public void onLtBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                CountryHolder h = (CountryHolder) holder;
//                h.l.setText("" + position);
//                h.r.setText("右边的" + position);
//            }
//        };
//        //禁用上拉加载:传一个什么都没有的View
//        new LtAdapter(new View(this));
//        //禁用下拉刷新:获取刷新View,并设置为不可用
//        view.getRefreshLayout().setEnabled(false);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                adapter.setOnRvItemClickListener(new OnRvItemClickListener() {
//                    @Override
//                    public void onItemClick(View itemView, int position) {
//                        //条目的点击事件
//                        Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainActivity.this, Main2Activity.class));
//                    }
//                });
//            }
//        }, 2000);
//        adapter.setOnRvItemLongClickListener(new OnRvItemLongClickListener() {
//            @Override
//            public void onItemLongClick(View itemView, int position) {
//                //条目的长按事件
//            }
//        });
        TextView textView = new TextView(this);
        textView.setText("我是头部局");
        rv.setNoItemText("123");
        adapter.addHeadView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("我是尾部局");
        adapter.addTailView(textView2);
//        contentView.getRecyclerView()
//                .setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        rv.addItemDecoration_line(10);
        rv.setAdapter(adapter);
        rv.setSpanCount(3)
                .setOnUpAndDownListener(new OnUpAndDownListener() {
                    @Override
                    public void up() {
                        //上拉加载时的回调
                        startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    }

                    @Override
                    public void down() {
                        //下拉刷新时的回调
                        adapter.removeHeadView(0);
                        TextView textView = new TextView(MainActivity.this);
                        textView.setText("我是头部局2");
                        adapter.addHeadView(textView);
                        adapter.notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 1000; i++) {
                                    list.add("" + i);
                                }
                                adapter.notifyDataSetChanged();
                                rv.setTopRefresh(false);
                            }
                        }, 2000);
                    }
                });
//        rv.setTopRefresh(true);
//        view.setSpanCount(3);
//        rv.setTopRefresh(false);
        rv.setBottomRefresh(false);
        adapter.setOnRvItemClickListener(new OnRvItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.e("lllttt", "click" + position);
            }
        });
        adapter.setOnRvItemLongClickListener(new OnRvItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                Log.e("lllttt", "longclick" + position);

            }
        });
//        contentView.getRecyclerView().setBackgroundResource(R.color.colorPrimary);
//        view.getRefreshLayout().setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.setTopRefresh(true);
                rv.setBottomRefresh(true);
            }
        }, 2000);
//        //添加头布局
//        adapter.addHeadView(new View(this));
//        //添加头布局到指定位置,注意可能会数组越界异常
//        adapter.addHeadView(new View(this), 0);
//        //移除头布局,根据引用地址
//        adapter.removeHeadView(view);
//        //移除头布局,根据索引,注意可能会数组越界异常
//        adapter.removeHeadView(0);
//        //获取头布局的数量
//        int headSize = adapter.getHeadListSize();
//
//        //添加尾布局(添加到最下面)
//        adapter.addTailView(new View(this));
//        //添加尾布局到指定位置,注意可能会数组越界异常
//        adapter.addTailView(new View(this), 0);
//        //移除尾布局,根据引用地址
//        adapter.removeTailView(view);
//        //移除尾布局,根据索引,注意可能会数组越界异常
//        adapter.removeTailView(0);
//        //获取尾布局的数量
//        int size = adapter.getTailListSize();

        //设置没数据时展示的TextView
//        view.setNoItemText("暂无数据");
        //设置没数据时展示的View
//        view.setNoItemView(new View(this));

        //没有条目时的回调
//        adapter.addOnNoItemListener(new OnNoItemListener() {
//            @Override
//            public void noItem() {
//                //从有数据变为没有数据时触发
//            }
//
//            @Override
//            public void haveItem() {
//                //从没有数据变为有数据是触发
//            }
//        });

//        //添加2px,d5d5d5的分割线
//        view.addItemDecoration_line();
//        //添加d5d5d5颜色的分割线,并指定高度
//        view.addItemDecoration_line(1);
//        //添加分割线,指定高度和颜色
//        view.addItemDecoration_line(1, getResources().getColor(R.color.colorAccent));
//        //添加图片分割线
//        view.addItemDecoration_drawable(R.mipmap.ic_launcher);

//        //设置分割线高度,可以和颜色一起设置
//        app:dividerHeight="1dp"
//        //设置分割线颜色
//        app:dividerColor="@color/colorAccent"
//        //设置图片分割线,和颜色,高度冲突
//        app:dividerDrawable="@mipmap/ic_launcher"

        //onClickTestLtRecyclerView(rv);
    }

    //测试下拉刷新
    public void onClickTestRefresh(View view) {
        startActivity(new Intent(this, Main3Activity.class));
    }

    public void onClickTestLtRecyclerView(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        TextView l;
        TextView r;

        public MViewHolder(View itemView) {
            super(itemView);
            l = (TextView) itemView.findViewById(R.id.tvLeft);
            r = (TextView) itemView.findViewById(R.id.tvRight);
        }
    }

    @Override
    public void onClick(View view) {
        /*SelectCountryActivity.setSelectListener(new Function1<String, Boolean>() {
            @Override
            public Boolean invoke(String s) {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                return true;
            }
        });*/

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list1.add("第一列" + i);
        }
        /*linkageManager = new Lt3LinkageManager(this, "请选择一个", list1, list2, list3)
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
                });*/
    }

    public void onClicka(View view) {
        Toast.makeText(this, "123", Toast.LENGTH_LONG).show();
    }
}
