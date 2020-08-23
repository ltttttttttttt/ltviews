package com.lt.ltviewsx.lt_recyclerview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.lt.ltviewsx.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创    建:  lt  2018/5/23--18:42
 * 作    用:  文字的下拉刷新
 * 注意事项:
 */
class MTextRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LtRefreshLayout(context, attrs, defStyleAttr) {
    private var tv: TextView? = null
    private var tvDate: TextView? = null
    private var iv: ImageView? = null
    private var oa: ObjectAnimator? = null
    private var date: String = justGetDate()
    private var sdf: SimpleDateFormat = SimpleDateFormat("M-d H:m")

    @SuppressLint("ObjectAnimatorBinding")
    override fun onState(state: RefreshStates) {
        when (state) {
            RefreshStates.STATE_REFRESH_DOWN -> {
                //下拉中
                tv?.setText(R.string.down_refresh)
                iv?.visibility = View.VISIBLE
                iv?.setImageResource(R.drawable.lt_arrow)
                //设置动画时间
                oa = ObjectAnimator.ofFloat(iv, "rotation", iv?.rotation ?: 0F, 0f)
                        .setDuration(animationTime.toLong())
                oa?.repeatCount = 0 //设置动画执行的次数
                oa?.start() //开始动画
            }
            RefreshStates.STATE_REFRESH_RELEASE -> {
                //松开刷新
                tv?.setText(R.string.release_refresh_now)
                iv?.visibility = View.VISIBLE
                iv?.setImageResource(R.drawable.lt_arrow)
                oa = ObjectAnimator.ofFloat(iv, "rotation", iv?.rotation ?: 0F, 180f)
                        .setDuration(animationTime.toLong()) //设置动画时间
                oa?.setRepeatCount(0) //设置动画执行的次数
                oa?.start() //开始动画
            }
            RefreshStates.STATE_REFRESHING -> {
                //刷新中
                tv?.setText(R.string.refresh)
                iv?.visibility = View.VISIBLE
                iv?.setImageResource(R.drawable.lt_loading)
                oa = ObjectAnimator.ofFloat(iv, "rotation", 0f, 360f)
                        .setDuration((animationTime shl 2.toLong().toInt()).toLong()) //设置动画时间
                oa?.setInterpolator(LinearInterpolator())
                oa?.setRepeatCount(ObjectAnimator.INFINITE) //设置动画执行的次数,这个是无限
                oa?.start() //开始动画
            }
            RefreshStates.STATE_REFRESH_FINISH -> {
                //刷新完成
                date = saveAndGetDate()
                tv?.setText(R.string.refresh_finish)
                tvDate?.text = context.getString(R.string.last_update) + date
                iv?.clearAnimation()
                oa?.cancel()
                iv?.visibility = View.INVISIBLE
                iv?.setImageResource(R.drawable.lt_arrow)
            }
            RefreshStates.STATE_BACK -> {
                //刷新头回到重新隐藏了回去,此时重置箭头的动画
                oa = ObjectAnimator.ofFloat(iv, "rotation", iv?.rotation ?: 0F, 0f)
                        .setDuration(animationTime.toLong())
                oa?.repeatCount = 0 //设置动画执行的次数
                oa?.start() //开始动画
            }
        }
    }

    override fun onProgress(y: Float) {}

    override fun createRefreshView(): View {
        val view = View.inflate(context, R.layout.lt_refresh_view, null)
        tv = view.findViewById<View>(R.id.tv_lt_refresh) as TextView
        tvDate = view.findViewById<View>(R.id.tv_lt_date) as TextView
        iv = view.findViewById<View>(R.id.iv_lt_refresh) as ImageView
        tvDate?.text = context.getString(R.string.last_update) + date
        return view
    }

    private fun justGetDate(): String {
        val preference = context.getSharedPreferences("lt_rv",
                Context.MODE_PRIVATE)
        return preference.getString("lt_date", "")
    }

    private fun saveAndGetDate(): String {
        val value = sdf.format(Date())
        val preference = context.getSharedPreferences("lt_rv",
                Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putString("lt_date", value)
        editor.apply()
        return value
    }
}