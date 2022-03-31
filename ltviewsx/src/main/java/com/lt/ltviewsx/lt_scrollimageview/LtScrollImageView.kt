package com.lt.ltviewsx.lt_scrollimageview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.lt.ltviewsx.R
import com.lt.ltviewsx.lt_listener.OnImageViewLoadUrlListener
import com.lt.ltviewsx.lt_listener.OnRvItemClickListener

/**
 * 创    建:  lt  2018/1/4--14:12
 * 作    用:  带指示器的无限滚动图片组件
 * 注意事项:
 */
class LtScrollImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val ltAdImageView: LtAdImageView//获取内部的LtAdGallery控件
    val llIndicator: LinearLayout//指示器的父布局
    private val dp10 = context.resources.getDimension(R.dimen.dp10).toInt()
    private var ivMargin = dp10

    init {
        val dp7 = context.resources.getDimension(R.dimen.dp7).toInt()
        ltAdImageView = LtAdImageView(context)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        ltAdImageView.layoutParams = lp
        addView(ltAdImageView)
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
     * @param imageUrl                  图片的网络路径数组 ,为空时 加载 adsId
     * @param switchTime                图片切换时间 小于等于0 为不自动切换
     * @param focusedId                 圆点选中时的背景ID
     * @param normalId                  圆点正常时的背景ID
     * @param listener                  设置加载图片的监听,用户自己来加载图片
     * @param onItemClickListener       条目点击事件
     * @param onItemChangeListener      条目当前展示的索引改变的回调
     */
    fun init(
        imageUrl: List<String?>,
        switchTime: Long,
        focusedId: Int,
        normalId: Int,
        listener: OnImageViewLoadUrlListener,
        onItemClickListener: OnRvItemClickListener?,
        onItemChangeListener: ((Int) -> Unit)?,
    ): LtScrollImageView {
        llIndicator.removeAllViews()
        val ivs = imageUrl.mapIndexed { index, _ ->
            val iv = ImageView(context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.leftMargin = ivMargin / 2
            layoutParams.rightMargin = ivMargin / 2
            iv.layoutParams = layoutParams
            if (index == 0)
                iv.setImageResource(focusedId)
            else
                iv.setImageResource(normalId)
            llIndicator.addView(iv)
            iv
        }

        ltAdImageView.initData(
            imageUrl,
            switchTime,
            listener,
            onItemClickListener
        ) {
            ivs.forEachIndexed { index, imageView ->
                if (index == it)
                    imageView.setImageResource(focusedId)
                else
                    imageView.setImageResource(normalId)
            }
            onItemChangeListener?.invoke(it)
        }
        return this
    }

    /**
     * 设置小圆点的间距
     */
    fun setIvMargin(margin: Int): LtScrollImageView {
        ivMargin = margin
        repeat(llIndicator.childCount) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.leftMargin = margin / 2
            layoutParams.rightMargin = margin / 2
            llIndicator.getChildAt(it).layoutParams = layoutParams
        }
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
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_TOP -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.TOP
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_TOP -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.TOP
                llIndicator.gravity = Gravity.RIGHT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.LEFT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_CENTER -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.CENTER_VERTICAL
                llIndicator.gravity = Gravity.RIGHT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.LEFT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.CENTER_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.RIGHT_BOTTOM -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.RIGHT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = 0
            }
            LtPosition.LEFT_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.LEFT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
            LtPosition.CENTER_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.CENTER_HORIZONTAL
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
            LtPosition.RIGHT_BOTTOM_OUT -> {
                (llIndicator.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                llIndicator.gravity = Gravity.RIGHT
                (ltAdImageView.layoutParams as LayoutParams).bottomMargin = dp10 * 2
            }
        }
        return this
    }
}