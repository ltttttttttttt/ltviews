package com.lt.ltviewsx.lt_scrollimageview

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lt.ltviewsx.R
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener
import com.lt.ltviewsx.lt_listener.OnScrollListener
import java.lang.Math.abs

/**
 * 无限滚动广告栏组件
 */
class LtAdGallery : Gallery, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener,
    View.OnTouchListener {
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
    private var mTimer = Handler(Looper.getMainLooper())

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
    private var firstX = 0f
    private var firstY = 0f
    private val dp8 = context.resources.getDimension(R.dimen.dp8)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    /**
     * 设置加载图片的监听,用户自己来加载图片
     */
    fun setOnImageViewLoadUrlListener(listener: OnImageViewLoadUrlListener?): LtAdGallery {
        this.listener = listener
        return this
    }

    /**
     * 圆点图片
     *
     * @param context    显示的Activity ,不能为null
     * @param mris       图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime 图片切换时间 写0 为不自动切换
     * @param aTime      动画时间
     * @param ovalLayout 圆点容器 ,可为空
     * @param focusedId  圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId   圆点正常时的背景ID,圆点容器为空写0
     */
    fun start(
        context: Context?, mris: List<String>?, switchTime: Int, aTime: Int,
        ovalLayout: LinearLayout?, focusedId: Int, normalId: Int
    ) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        mOvalLayout = ovalLayout
        mFocusedId = focusedId
        mNormalId = normalId
        ininImages() // 初始化图片组
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
        if (count > 0)
            setSelection(count / 2 / getImageUrlSize() * getImageUrlSize()) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    /**
     * 圆点数量的文字
     */
    fun start(
        context: Context?, mris: List<String>?, switchTime: Int,
        tv_page: TextView?
    ) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        this.tv_page = tv_page
        ininImages() // 初始化图片组
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
        if (count > 0)
            setSelection(count / 2 / getImageUrlSize() * getImageUrlSize()) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    fun start(
        context: Context?, mris: List<String>?, switchTime: Int,
        tv_page: TextView?, img_video: ImageView?
    ) {
        mContext = context
        mUris = mris
        mSwitchTime = switchTime
        this.tv_page = tv_page
        this.img_video = img_video
        ininImages() // 初始化图片组
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
        if (count > 0)
            setSelection(count / 2 / getImageUrlSize() * getImageUrlSize()) // 默认选中中间位置为起始位置
        isFocusableInTouchMode = true
        initOvalLayout() // 初始化圆点
        startTimer() // 开始自动滚动任务
    }

    fun clear() {
        listImgs?.clear()
        (adapter as? BaseAdapter)?.notifyDataSetChanged()
        mOvalLayout?.removeAllViews()
        stopTimer()
    }

    /**
     * 初始化图片组
     */
    private fun ininImages() {
        listImgs?.clear()
        if (mUris.isNullOrEmpty()) {
            clear()
            return
        }
        val len = mUris?.size ?: 0
        for (i in 0 until len) {
            val imageview = ImageView(mContext) // 实例化ImageView的对象
            imageview.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            imageview.scaleType = ImageView.ScaleType.CENTER_CROP
            listener?.onLoad(imageview, mUris!![i])
            listImgs?.add(imageview)
        }
    }

    /**
     * 初始化圆点
     */
    private fun initOvalLayout() {
        if (mOvalLayout != null && getImageUrlSize() < 2) {
            // 如果只有一第图时不显示圆点容器
            mOvalLayout?.layoutParams?.height = 0
        } else if (mOvalLayout != null) {
            // 圆点的左右外边距是 圆点窗口的 20%;
            if (ovalmargin == 0) ovalmargin = try {
                (BitmapFactory.decodeResource(resources, mFocusedId).width * 0.3).toInt()
            } catch (e: Exception) {
                10
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(ovalmargin, 0, ovalmargin, 0)
            mOvalLayout?.removeAllViews()
            for (i in 0 until getImageUrlSize()) {
                val v = ImageView(mContext) // 员点
                v.layoutParams = layoutParams
                v.setImageResource(mNormalId)
                mOvalLayout?.addView(v)
            }
            // 选中第一个
            (mOvalLayout?.getChildAt(0) as? ImageView)?.setImageResource(mFocusedId)
        }
        if (tv_page != null) {
            tv_page?.text = "1/${getImageUrlSize()}"
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (listImgs?.isNotEmpty() == true) {
            startTimer()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTimer()
    }

    //解决索引越界bug
    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val childPosition = super.getChildDrawingOrder(childCount, i)
        if (childPosition >= childCount)
            return childCount - 1
        return childPosition
    }

    /**
     * 无限循环适配器
     */
    internal inner class AdAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return if (listImgs.isNullOrEmpty()) 0 else Int.MAX_VALUE
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return listImgs!![position % getImageUrlSize()] // 返回ImageView
        }

        override fun getItem(position: Int): Any {
            return listImgs!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float,
        velocityY: Float
    ): Boolean {
        val kEvent: Int = if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
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
        if (getImageUrlSize() == 1) {
            if (MotionEvent.ACTION_DOWN == event.action) {
                firstX = event.rawX
                firstY = event.rawY
            } else if (MotionEvent.ACTION_UP == event.action
                || MotionEvent.ACTION_CANCEL == event.action
            ) {
                if (abs(event.rawX - firstX) <= dp8 && abs(event.rawY - firstY) <= dp8)
                    mOnRvItemClickListener?.onItemClick(listImgs!![0], 0)
            }
            return true
        }
        if (MotionEvent.ACTION_UP == event.action
            || MotionEvent.ACTION_CANCEL == event.action
        ) {
            startTimer() // 开始自动滚动任务
        } else {
            stopTimer() // 停止自动滚动任务
        }
        return false
    }

    /**
     * 图片切换事件
     */
    override fun onItemSelected(
        arg0: AdapterView<*>?, arg1: View?, position: Int,
        arg3: Long
    ) {
        curIndex = position % getImageUrlSize()
        if (mOvalLayout != null && getImageUrlSize() > 1) { // 切换圆点
            (mOvalLayout?.getChildAt(oldIndex) as? ImageView)?.setImageResource(mNormalId) // 圆点取消
            (mOvalLayout?.getChildAt(curIndex) as? ImageView)?.setImageResource(mFocusedId) // 圆点选中
            oldIndex = curIndex
        }
        if (tv_page != null) {
            tv_page?.text = "${curIndex + 1}/${getImageUrlSize()}"
        }
        if (img_video != null) {
            if (mUris?.get(curIndex)?.contains("/video/") == true) {
                img_video?.visibility = View.VISIBLE
                img_video?.tag = curIndex
            } else {
                img_video?.visibility = View.GONE
                img_video?.tag = -1
            }
        }
        mOnScrollListener?.onScroll(position)
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}

    /**
     * 项目点击事件
     */
    override fun onItemClick(
        arg0: AdapterView<*>?, arg1: View, position: Int,
        arg3: Long
    ) {
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
        mTimer.removeCallbacksAndMessages(null)
    }

    /**
     * 开始自动滚动任务 图片大于1张才滚动
     */
    fun startTimer() {
        if (getImageUrlSize() > 1 && mSwitchTime > 0) {
            stopTimer()
            lateinit var runnable: Runnable
            runnable = Runnable {
                onScroll(null, null, 1f, 0f)
                onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null)
                mTimer.postDelayed(runnable, mSwitchTime.toLong())
            }
            mTimer.postDelayed(runnable, mSwitchTime.toLong())
        }
    }

    /**
     * 设置小圆点的边距
     */
    fun setIvMargin(margin: Int) {
        ovalmargin = margin
        initOvalLayout()
    }

    /**
     * 获取图片的数量
     */
    fun getImageUrlSize(): Int = listImgs?.size ?: 0
}