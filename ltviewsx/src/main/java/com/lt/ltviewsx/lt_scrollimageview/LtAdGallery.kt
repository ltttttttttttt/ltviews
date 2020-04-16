package com.lt.ltviewsx.lt_scrollimageview

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener
import com.lt.ltviewsx.lt_listener.OnScrollListener
import java.util.*

/**
 * 无限滚动广告栏组件
 */
class LtAdGallery : Gallery, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {
    //	private ImageLoader imageLoader = ImageLoader.getInstance();
//	private DisplayImageOptions options = new DisplayImageOptions.Builder()
//			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
//			.bitmapConfig(Bitmap.Config.RGB_565).build();
    /**
     * 显示的Activity
     */
    private var mContext: Context? = null

    /**
     * 条目单击事件接口
     */
    private var mOnRvItemClickListener: OnRvItemClickListener? = null

    /**
     * 滚动事件监听
     */
    private var mOnScrollListener: OnScrollListener? = null

    /**
     * 图片切换时间
     */
    private var mSwitchTime = 0

    /**
     * 自动滚动的定时器
     */
    private var mTimer: Timer? = null

    /**
     * 圆点容器
     */
    private var mOvalLayout: LinearLayout? = null

    /**
     * item数量
     */
    private var tv_page: TextView? = null

    /**
     * 视频按钮
     */
    private var img_video: ImageView? = null

    /**
     * 当前选中的数组索引
     */
    private var curIndex = 0

    /**
     * 上次选中的数组索引
     */
    private var oldIndex = 0

    /**
     * 圆点选中时的背景ID
     */
    private var mFocusedId = 0

    /**
     * 圆点正常时的背景ID
     */
    private var mNormalId = 0

    /**
     * 图片资源ID组
     */
    private var mUris: List<String>? = null

    /**
     * ImageView组
     */
    var listImgs: MutableList<View>? = ArrayList() // 图片组;

    /**
     * 加载图片的监听
     */
    private var listener: OnImageViewLoadUrlListener? = null
    private var ovalmargin = 0

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    /**
     * 设置加载图片的监听,用户自己来加载图片
     */
    fun setOnImageViewLoadUrlListener(listener: OnImageViewLoadUrlListener?): LtAdGallery {
        this.listener = listener
        return this
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
    fun start(context: Context?, mris: List<String>?, switchTime: Int, aTime: Int,
              ovalLayout: LinearLayout?, focusedId: Int, normalId: Int) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        mOvalLayout = ovalLayout
        mFocusedId = focusedId
        mNormalId = normalId
        ininImages(context) // 初始化图片组
        if (adapter !is BaseAdapter)
            adapter = AdAdapter()
        else
            (adapter as BaseAdapter).notifyDataSetChanged()
        this.onItemClickListener = this
        setOnTouchListener(this)
        this.onItemSelectedListener = this
        this.isSoundEffectsEnabled = false
        setAnimationDuration(aTime) // 动画时间
        setUnselectedAlpha(1f) // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0)
        // 取靠近中间 图片数组的整倍数
        setSelection(count / 2 / listImgs!!.size * listImgs!!.size) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    fun start(context: Context?, mris: List<String>?, switchTime: Int,
              tv_page: TextView?) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        this.tv_page = tv_page
        ininImages(context) // 初始化图片组
        if (adapter !is BaseAdapter)
            adapter = AdAdapter()
        else
            (adapter as BaseAdapter).notifyDataSetChanged()
        this.onItemClickListener = this
        setOnTouchListener(this)
        this.onItemSelectedListener = this
        this.isSoundEffectsEnabled = false
        setAnimationDuration(1666) // 动画时间
        setUnselectedAlpha(1f) // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0)
        // 取靠近中间 图片数组的整倍数
        setSelection(count / 2 / listImgs!!.size * listImgs!!.size) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    fun start(context: Context?, mris: List<String>?, switchTime: Int,
              tv_page: TextView?, img_video: ImageView?) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        this.tv_page = tv_page
        this.img_video = img_video
        ininImages(context) // 初始化图片组
        if (adapter !is BaseAdapter)
            adapter = AdAdapter()
        else
            (adapter as BaseAdapter).notifyDataSetChanged()
        this.onItemClickListener = this
        setOnTouchListener(this)
        this.onItemSelectedListener = this
        this.isSoundEffectsEnabled = false
        setAnimationDuration(1666) // 动画时间
        setUnselectedAlpha(1f) // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0)
        // 取靠近中间 图片数组的整倍数
        setSelection(count / 2 / listImgs!!.size * listImgs!!.size) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    fun clear() {
        if (listImgs != null) {
            listImgs!!.clear()
        }
        (adapter as BaseAdapter).notifyDataSetChanged()
        mOvalLayout!!.removeAllViews()
        stopTimer()
    }

    /**
     * 初始化图片组
     */
    private fun ininImages(context: Context?) {
        listImgs!!.clear()
        if (mUris == null || mUris!!.size == 0) {
            clear()
            return
        }
        val len = mUris!!.size
        for (i in 0 until len) {
            val imageview = ImageView(mContext) // 实例化ImageView的对象
            imageview.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT)
            imageview.scaleType = ImageView.ScaleType.CENTER_CROP
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
            if (listener != null) listener!!.onLoad(imageview, mUris!![i])
            //            Picasso.with(context).load(mUris[i])/*.placeholder(R.mipmap.ic_launcher)*/.into(imageview);
//			imageLoader.displayImage(mUris[i], imageview, options, null);
// listImgs.add(imageview);
            listImgs!!.add(imageview)
        }
    }

    /**
     * 初始化圆点
     */
    private fun initOvalLayout() {
        if (mOvalLayout != null && listImgs!!.size < 2) { // 如果只有一第图时不显示圆点容器
            mOvalLayout!!.layoutParams.height = 0
        } else if (mOvalLayout != null) { // 圆点的大小是 圆点窗口的 70%;
//            int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.7);
// 圆点的左右外边距是 圆点窗口的 20%;
//            int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
            if (ovalmargin == 0) ovalmargin = try {
                (BitmapFactory.decodeResource(resources, mFocusedId).width * 0.3).toInt()
            } catch (e: Exception) {
                10
            }
            val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(ovalmargin, 0, ovalmargin, 0)
            mOvalLayout!!.removeAllViews()
            for (i in listImgs!!.indices) {
                val v = ImageView(mContext) // 员点
                v.layoutParams = layoutParams
                v.setImageResource(mNormalId)
                mOvalLayout!!.addView(v)
            }
            println(mOvalLayout!!.childCount)
            // 选中第一个
            (mOvalLayout!!.getChildAt(0) as ImageView).setImageResource(mFocusedId)
        }
        if (tv_page != null) {
            tv_page!!.text = 1.toString() + "/" + listImgs!!.size
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
    internal inner class AdAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return if (listImgs!!.size < 2) listImgs!!.size else Int.MAX_VALUE
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return listImgs!![position % listImgs!!.size] // 返回ImageView
        }

        override fun getItem(position: Int): Any {
            return listImgs!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                         velocityY: Float): Boolean {
        val kEvent: Int
        kEvent = if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
            KeyEvent.KEYCODE_DPAD_LEFT
        } else { // 检查是否往右滑动
            KeyEvent.KEYCODE_DPAD_RIGHT
        }
        onKeyDown(kEvent, null)
        return true
    }

    /**
     * 检查是否往左滑动
     */
    private fun isScrollingLeft(e1: MotionEvent, e2: MotionEvent): Boolean {
        return e2.x > e1.x + 50
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_UP == event.action
                || MotionEvent.ACTION_CANCEL == event.action) {
            startTimer() // 开始自动滚动任务
        } else {
            stopTimer() // 停止自动滚动任务
        }
        return false
    }

    /**
     * 图片切换事件
     */
    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int,
                                arg3: Long) {
        curIndex = position % listImgs!!.size
        if (mOvalLayout != null && listImgs!!.size > 1) { // 切换圆点
//            mOvalLayout.getChildAt(oldIndex).setBackgroundResource(mNormalId); // 圆点取消
//            mOvalLayout.getChildAt(curIndex).setBackgroundResource(mFocusedId);// 圆点选中
            (mOvalLayout!!.getChildAt(oldIndex) as ImageView).setImageResource(mNormalId) // 圆点取消
            (mOvalLayout!!.getChildAt(curIndex) as ImageView).setImageResource(mFocusedId) // 圆点选中
            oldIndex = curIndex
        }
        if (tv_page != null) {
            tv_page!!.text = (curIndex + 1).toString() + "/" + listImgs!!.size
        }
        if (img_video != null) {
            if (mUris!![curIndex].contains("/video/")) { // img_video.setVisibility(View.VISIBLE);
                img_video!!.visibility = View.GONE
                img_video!!.tag = curIndex
            } else {
                img_video!!.visibility = View.GONE
                img_video!!.tag = -1
            }
        }
        if (mOnScrollListener != null) mOnScrollListener!!.onScroll(position)
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}

    /**
     * 项目点击事件
     */
    override fun onItemClick(arg0: AdapterView<*>?, arg1: View, position: Int,
                             arg3: Long) {
        if (mOnRvItemClickListener != null) {
            mOnRvItemClickListener!!.onItemClick(arg1, curIndex)
        }
    }

    /**
     * 设置项目点击事件监听器
     */
    fun setOnRvItemClickListener(listener: OnRvItemClickListener?) {
        mOnRvItemClickListener = listener
    }

    /**
     * 设置滚动条目监听
     */
    fun setOnScrollListener(listener: OnScrollListener?) {
        mOnScrollListener = listener
    }

    /**
     * 停止自动滚动任务
     */
    fun stopTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    /**
     * 开始自动滚动任务 图片大于1张才滚动
     */
    fun startTimer() {
        if (mTimer == null && listImgs!!.size > 1 && mSwitchTime > 0) {
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    handler.post {
                        onScroll(null, null, 1f, 0f)
                        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null)
                    }
                }
            }, mSwitchTime.toLong(), mSwitchTime.toLong())
        }
    }

    /**
     * 设置小圆点的边距
     */
    fun setIvMargin(margin: Int) {
        ovalmargin = margin
        initOvalLayout()
    }
}