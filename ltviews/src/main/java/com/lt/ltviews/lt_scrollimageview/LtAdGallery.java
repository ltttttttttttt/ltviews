package com.lt.ltviews.lt_scrollimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lt.ltviews.lt_listener.OnRvItemClickListener;
import com.lt.ltviews.lt_listener.OnScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 无限滚动广告栏组件
 */
public class LtAdGallery extends Gallery implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, OnTouchListener {
//	private ImageLoader imageLoader = ImageLoader.getInstance();
//	private DisplayImageOptions options = new DisplayImageOptions.Builder()
//			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
//			.bitmapConfig(Bitmap.Config.RGB_565).build();

    /**
     * 显示的Activity
     */
    private Context mContext;
    /**
     * 条目单击事件接口
     */
    private OnRvItemClickListener mOnRvItemClickListener;
    /**
     * 滚动事件监听
     */
    private OnScrollListener mOnScrollListener;
    /**
     * 图片切换时间
     */
    private int mSwitchTime;
    /**
     * 自动滚动的定时器
     */
    private Timer mTimer;
    /**
     * 圆点容器
     */
    private LinearLayout mOvalLayout;
    /**
     * item数量
     */
    private TextView tv_page;
    /**
     * 视频按钮
     */
    private ImageView img_video;
    /**
     * 当前选中的数组索引
     */
    private int curIndex = 0;
    /**
     * 上次选中的数组索引
     */
    private int oldIndex = 0;
    /**
     * 圆点选中时的背景ID
     */
    private int mFocusedId;
    /**
     * 圆点正常时的背景ID
     */
    private int mNormalId;
    /**
     * 默认图或图片失败时的背景ID
     */
    private int backgroundId;
    /**
     * 图片资源ID组
     */
    private String[] mUris;
    /**
     * ImageView组
     */
    List<View> listImgs = new ArrayList<View>(); // 图片组;
    private int ovalmargin = 0;

    public LtAdGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LtAdGallery(Context context) {
        super(context);
    }

    public LtAdGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context    显示的Activity ,不能为null
     * @param mris       图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime 图片切换时间 写0 为不自动切换
     * @param aTime      动画时间
     * @param ovalLayout 圆点容器 ,可为空
     * @param focusedId  圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId   圆点正常时的背景ID,圆点容器为空写0
     */
    public void start(Context context, String[] mris, int switchTime, int aTime,
                      LinearLayout ovalLayout, int focusedId, int normalId, int backgroundId) {
        this.mContext = context;
        this.mUris = mris;
        this.mSwitchTime = switchTime;
        this.mOvalLayout = ovalLayout;
        this.mFocusedId = focusedId;
        this.mNormalId = normalId;
        this.backgroundId = backgroundId;
        ininImages(context);// 初始化图片组
        setAdapter(new AdAdapter());
        this.setOnItemClickListener(this);
        this.setOnTouchListener(this);
        this.setOnItemSelectedListener(this);
        this.setSoundEffectsEnabled(false);
        this.setAnimationDuration(aTime); // 动画时间
        this.setUnselectedAlpha(1); // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0);
        // 取靠近中间 图片数组的整倍数
        setSelection((getCount() / 2 / listImgs.size()) * listImgs.size()); // 默认选中中间位置为起始位置
        setFocusableInTouchMode(true);
        initOvalLayout();// 初始化圆点
        startTimer();// 开始自动滚动任务
    }

    public void start(Context context, String[] mris, int switchTime,
                      TextView tv_page) {
        this.mContext = context;
        this.mUris = mris;
        this.mSwitchTime = switchTime;
        this.tv_page = tv_page;
        ininImages(context);// 初始化图片组
        setAdapter(new AdAdapter());
        this.setOnItemClickListener(this);
        this.setOnTouchListener(this);
        this.setOnItemSelectedListener(this);
        this.setSoundEffectsEnabled(false);
        this.setAnimationDuration(1666); // 动画时间
        this.setUnselectedAlpha(1); // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0);
        // 取靠近中间 图片数组的整倍数
        setSelection((getCount() / 2 / listImgs.size()) * listImgs.size()); // 默认选中中间位置为起始位置
        setFocusableInTouchMode(true);
        initOvalLayout();// 初始化圆点
        startTimer();// 开始自动滚动任务
    }

    public void start(Context context, String[] mris, int switchTime,
                      TextView tv_page, ImageView img_video) {
        this.mContext = context;
        this.mUris = mris;
        this.mSwitchTime = switchTime;
        this.tv_page = tv_page;
        this.img_video = img_video;
        ininImages(context);// 初始化图片组
        setAdapter(new AdAdapter());
        this.setOnItemClickListener(this);
        this.setOnTouchListener(this);
        this.setOnItemSelectedListener(this);
        this.setSoundEffectsEnabled(false);
        this.setAnimationDuration(1666); // 动画时间
        this.setUnselectedAlpha(1); // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0);
        // 取靠近中间 图片数组的整倍数
        setSelection((getCount() / 2 / listImgs.size()) * listImgs.size()); // 默认选中中间位置为起始位置
        setFocusableInTouchMode(true);
        initOvalLayout();// 初始化圆点
        startTimer();// 开始自动滚动任务
    }

    public void clear() {
        if (listImgs != null) {
            listImgs.clear();
        }
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
        mOvalLayout.removeAllViews();
        stopTimer();
    }

    /**
     * 初始化图片组
     */
    private void ininImages(Context context) {

        listImgs.clear();
        if (mUris == null || mUris.length == 0) {
            clear();
            return;
        }
        int len = mUris.length;
        for (int i = 0; i < len; i++) {
            ImageView imageview = new ImageView(mContext); // 实例化ImageView的对象
            imageview.setLayoutParams(new Gallery.LayoutParams(
                    Gallery.LayoutParams.MATCH_PARENT,
                    Gallery.LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // FinalBitmap.create(mContext).display(imageview, mUris[i],
            // imageview.getWidth(), imageview.getHeight(), null, null);

            // imageview.setImageResource(R.drawable.gg01);

//            View view = View.inflate(context, R.layout.item_my_gallery, null);
//            ImageView imageview = (ImageView) view
//                    .findViewById(R.id.img_content);


            // ImageView img_video = (ImageView)
            // view.findViewById(R.id.img_video);
            // if (mUris[i].contains("/video/"))
            // {
            // img_video.setVisibility(View.VISIBLE);
            // } else
            // {
            // img_video.setVisibility(View.GONE);
            // }
            Glide.with(context)
                    .load(mUris[i])
                    .placeholder(backgroundId)
                    .error(backgroundId)
                    .centerCrop()
                    .into(imageview);
//            Picasso.with(context).load(mUris[i])/*.placeholder(R.mipmap.ic_launcher)*/.into(imageview);
//			imageLoader.displayImage(mUris[i], imageview, options, null);
            // listImgs.add(imageview);
            listImgs.add(imageview);
        }

    }

    /**
     * 初始化圆点
     */
    private void initOvalLayout() {
        if (mOvalLayout != null && listImgs.size() < 2) {// 如果只有一第图时不显示圆点容器
            mOvalLayout.getLayoutParams().height = 0;
        } else if (mOvalLayout != null) {
            // 圆点的大小是 圆点窗口的 70%;
//            int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.7);
            // 圆点的左右外边距是 圆点窗口的 20%;
//            int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
            if (ovalmargin == 0)
                try {
                    ovalmargin = (int) (BitmapFactory.decodeResource(getResources(), mFocusedId).getWidth() * 0.3);
                } catch (Exception e) {
                    ovalmargin = 10;
                }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ovalmargin, 0, ovalmargin, 0);

            mOvalLayout.removeAllViews();
            for (int i = 0; i < listImgs.size(); i++) {
                ImageView v = new ImageView(mContext); // 员点
                v.setLayoutParams(layoutParams);
                v.setImageResource(mNormalId);
                mOvalLayout.addView(v);
            }

            System.out.println(mOvalLayout.getChildCount());
            // 选中第一个
            ((ImageView) mOvalLayout.getChildAt(0)).setImageResource(mFocusedId);
        }
        if (tv_page != null) {
            tv_page.setText(1 + "/" + listImgs.size());
        }
    }
// /** 初始化圆点 */
//    private void initOvalLayout() {
//        if (mOvalLayout != null && listImgs.size() < 2) {// 如果只有一第图时不显示圆点容器
//            mOvalLayout.getLayoutParams().height = 0;
//        } else if (mOvalLayout != null) {
//            // 圆点的大小是 圆点窗口的 70%;
//            int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.7);
//            // 圆点的左右外边距是 圆点窗口的 20%;
//            int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    Ovalheight, Ovalheight);
//            layoutParams.setMargins(Ovalmargin, 0, Ovalmargin, 0);
//
//            mOvalLayout.removeAllViews();
//            for (int i = 0; i < listImgs.size(); i++) {
//                View v = new View(mContext); // 员点
//                v.setLayoutParams(layoutParams);
//                v.setBackgroundResource(mNormalId);
//                mOvalLayout.addView(v);
//            }
//
//            System.out.println(mOvalLayout.getChildCount());
//            // 选中第一个
//            mOvalLayout.getChildAt(0).setBackgroundResource(mFocusedId);
//        }
//        if (tv_page != null) {
//            tv_page.setText(1 + "/" + listImgs.size());
//        }
//    }

    /**
     * 无限循环适配器
     */
    class AdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (listImgs.size() < 2)// 如果只有一张图时不滚动
                return listImgs.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return listImgs.get(position % listImgs.size()); // 返回ImageView
        }

        @Override
        public Object getItem(int position) {
            return listImgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int kEvent;
        if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { // 检查是否往右滑动
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;

    }

    /**
     * 检查是否往左滑动
     */
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > (e1.getX() + 50);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()
                || MotionEvent.ACTION_CANCEL == event.getAction()) {
            startTimer();// 开始自动滚动任务
        } else {
            stopTimer();// 停止自动滚动任务
        }
        return false;
    }

    /**
     * 图片切换事件
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        curIndex = position % listImgs.size();
        if (mOvalLayout != null && listImgs.size() > 1) { // 切换圆点
//            mOvalLayout.getChildAt(oldIndex).setBackgroundResource(mNormalId); // 圆点取消
//            mOvalLayout.getChildAt(curIndex).setBackgroundResource(mFocusedId);// 圆点选中
            ((ImageView) mOvalLayout.getChildAt(oldIndex)).setImageResource(mNormalId); // 圆点取消
            ((ImageView) mOvalLayout.getChildAt(curIndex)).setImageResource(mFocusedId);// 圆点选中
            oldIndex = curIndex;
        }
        if (tv_page != null) {
            tv_page.setText((curIndex + 1) + "/" + listImgs.size());
        }
        if (img_video != null) {
            if (mUris[curIndex].contains("/video/")) {
                // img_video.setVisibility(View.VISIBLE);
                img_video.setVisibility(View.GONE);
                img_video.setTag(curIndex);
            } else {
                img_video.setVisibility(View.GONE);
                img_video.setTag(-1);
            }
        }
        if (mOnScrollListener != null)
            mOnScrollListener.onScroll(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * 项目点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (mOnRvItemClickListener != null) {
            mOnRvItemClickListener.onItemClick(arg1, curIndex);
        }
    }

    /**
     * 设置项目点击事件监听器
     */
    public void setOnRvItemClickListener(OnRvItemClickListener listener) {
        mOnRvItemClickListener = listener;
    }

    /**
     * 设置滚动条目监听
     */
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    /**
     * 停止自动滚动任务
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 开始自动滚动任务 图片大于1张才滚动
     */
    public void startTimer() {
        if (mTimer == null && listImgs.size() > 1 && mSwitchTime > 0) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public void run() {
                    handler.sendMessage(handler.obtainMessage(1));
                }
            }, mSwitchTime, mSwitchTime);
        }
    }

    /**
     * 处理定时滚动任务
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 不包含spacing会导致onKeyDown()失效!!!
            // 失效onKeyDown()前先调用onScroll(null,1,0)可处理
            onScroll(null, null, 1, 0);
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        }
    };

    /**
     * 设置小圆点的边距
     */
    public void setIvMargin(int margin) {
        ovalmargin = margin;
        initOvalLayout();
    }
}
