package com.lt.ltviewsx.lt_scrollimageview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener


/**
 * creator: lt  2022/3/29  lt.dygzs@qq.com
 * effect : 无限滚动图片组件
 * warning:
 */
class LtAdImageView : ViewPager {
    private var switchTime = 3000L
    private var isAutoLoop = false//是否自动滚动
    private val timerRunnable = object : Runnable {
        override fun run() {
            currentItem++
            postDelayed(this, switchTime)
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        offscreenPageLimit = 0
    }

    /**
     * 初始化数据
     *
     * @param images                    图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime                图片自动切换时间 小于等于0 为不自动切换
     * @param onLoadImageListener       图片加载的回调
     * @param onItemClickListener       条目点击事件
     * @param onItemChangeListener      条目当前展示的索引改变的回调
     */
    fun initData(
        images: List<String?>,
        switchTime: Long,
        onLoadImageListener: OnImageViewLoadUrlListener,
        onItemClickListener: OnRvItemClickListener?,
        onItemChangeListener: ((Int) -> Unit)?,
    ) {
        stop()
        setOnPageChangeListener(null)
        initAdapter(images, onLoadImageListener, onItemClickListener, onItemChangeListener)
        currentItem = images.size * 10000
        this.switchTime = switchTime
        start()
    }

    /**
     * 手动开始自动滚动,调用前先调用stop()
     */
    fun start() {
        isAutoLoop = true
        if (switchTime > 0L)
            postDelayed(timerRunnable, switchTime)
        else
            mStop()
    }

    /**
     * 停止自动滚动
     */
    fun stop() {
        isAutoLoop = false
        mStop()
    }

    private fun mStop() {
        removeCallbacks(timerRunnable)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_UP || ev?.action == MotionEvent.ACTION_CANCEL) {
            if (isAutoLoop) {
                mStop()
                start()
            }
        } else {
            mStop()
        }
        return super.onTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAutoLoop) {
            mStop()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mStop()
    }

    private fun initAdapter(
        images: List<String?>,
        onLoadImageListener: OnImageViewLoadUrlListener,
        onItemClickListener: OnRvItemClickListener?,
        onItemChangeListener: ((Int) -> Unit)?
    ) {
        adapter = null
        setOnPageChangeListener(null)
        //根据vp特性,做view缓存
        val multiple = if (images.size == 1) 3 else if (images.size == 2) 2 else 1
        val views = (0 until multiple).map {
            images.mapIndexed { index, imgaeUrl ->
                val iv = ImageView(context)
                iv.layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                iv.scaleType = ImageView.ScaleType.CENTER_CROP

                onLoadImageListener.onLoad(iv, imgaeUrl)
                if (onItemClickListener != null)
                    iv.setOnClickListener {
                        onItemClickListener.onItemClick(iv, index)
                    }

                iv
            }
        }.flatten()

        adapter = AdImageAdapter(views)
        if (onItemChangeListener != null)
            setOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (multiple == 3)
                        onItemChangeListener(0)
                    else if (multiple == 2)
                        onItemChangeListener(position % images.size % multiple)
                    else
                        onItemChangeListener(position % images.size)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
    }

    //优化性能后的适配器
    //如果条目数量只有1,就放三个相同的条目
    //如果条目数量只有2,就放双倍的条目
    //其他正常放
    private inner class AdImageAdapter(val views: List<ImageView>) : PagerAdapter() {
        override fun getCount(): Int = if (views.isNullOrEmpty()) 0 else Int.MAX_VALUE

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val iv = views[position % views.size]
            (iv.parent as? ViewGroup)?.removeView(iv)
            container.addView(iv)
            return iv
        }
    }
}