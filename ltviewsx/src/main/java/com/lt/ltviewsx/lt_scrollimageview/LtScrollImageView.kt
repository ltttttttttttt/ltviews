package com.lt.ltviewsx.lt_scrollimageview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.lt.ltviewsx.R
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener
import com.lt.ltviewsx.lt_listener.OnScrollListener

/**
 * 创    建:  lt  2018/1/4--14:12
 * 作    用:
 * 注意事项:
 */
class LtScrollImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    val ltAdGallery: LtAdGallery//获取内部的LtAdGallery控件
    val llIndicator: LinearLayout//指示器的父布局
    val dp10 = context.resources.getDimension(R.dimen.dp10).toInt()

    init {
        val dp7 = context.resources.getDimension(R.dimen.dp7).toInt()
        ltAdGallery = LtAdGallery(context)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        ltAdGallery.layoutParams = lp
        addView(ltAdGallery)
        llIndicator = LinearLayout(context)
        val lp2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        llIndicator.setPadding(dp10, dp7, dp10, dp7)
        llIndicator.orientation = LinearLayout.HORIZONTAL
        lp2.gravity = Gravity.BOTTOM
        llIndicator.gravity = Gravity.CENTER_HORIZONTAL
        llIndicator.layoutParams = lp2
        addView(llIndicator)
    }

    /**
     * 初始化
     *
     * @param imageUrl   图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime 图片切换时间 写0 为不自动切换
     * @param aTime      中间过度的动画播放时间
     * @param focusedId  圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId   圆点正常时的背景ID,圆点容器为空写0
     * @param listener   设置加载图片的监听,用户自己来加载图片
     */
    fun init(imageUrl: List<String>?, switchTime: Int, aTime: Int, focusedId: Int, normalId: Int, listener: OnImageViewLoadUrlListener?): LtScrollImageView {
        ltAdGallery.setOnImageViewLoadUrlListener(listener)
                .start(context, imageUrl, switchTime, aTime, llIndicator, focusedId, normalId)
        return this
    }

    /**
     * 设置条目点击事件
     */
    fun setOnRvItemClickListener(onRvItemClickListener: OnRvItemClickListener?): LtScrollImageView {
        ltAdGallery.setOnRvItemClickListener(onRvItemClickListener)
        return this
    }

    /**
     * 设置小圆点的间距
     */
    fun setIvMargin(margin: Int): LtScrollImageView {
        ltAdGallery.setIvMargin(margin)
        return this
    }

    /**
     * 设置滚动条目监听
     */
    fun setOnScrollListener(listener: OnScrollListener?): LtScrollImageView {
        ltAdGallery.setOnScrollListener(listener)
        return this
    }

    /**
     * 设置小圆点的位置,默认居中靠下
     */
    fun setPosition(ltSIVPosition: LtPosition): LtScrollImageView {
        when (ltSIVPosition) {
            LtPosition.LEFT_TOP -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.TOP
                llIndicator.gravity = Gravity.LEFT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_TOP -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.TOP
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_TOP -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.TOP
                llIndicator.gravity = Gravity.RIGHT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.LEFT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.RIGHT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.LEFT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.RIGHT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.LEFT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
            LtPosition.CENTER_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
            LtPosition.RIGHT_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.RIGHT
                (ltAdGallery.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
        }
        return this
    }
}