package com.goldmedal.hrapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.transform.PageTransformerFactory
import com.goldmedal.hrapp.common.transform.TransformerStyle
import com.goldmedal.hrapp.data.adapters.IntroAdapter
import com.goldmedal.hrapp.data.model.CustomBean
import com.goldmedal.hrapp.data.model.viewholder.CustomPageViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

@AndroidEntryPoint
class IntroActivity : AppCompatActivity(){



    private val viewModel: LoginViewModel by viewModels()
    private lateinit var mViewPager: BannerViewPager<CustomBean, CustomPageViewHolder>

        private val des = arrayOf("Goldmedal is synonymous with\nworld-class electrical brands", "Switch to the amazing\nto come across products of tomorrow", "We've been innovating since\ninception in 1979 \nIt's a legacy we are proud of and \nwhich will never change")
    private val introJson = arrayOf(R.raw.splash_1, R.raw.splash_2, R.raw.splash_3)
    private val transforms = intArrayOf( TransformerStyle.ACCORDION, TransformerStyle.DEPTH, TransformerStyle.ROTATE, TransformerStyle.SCALE_IN)
    private val backgroundRes = intArrayOf( R.color.colorMaterialIndigo, R.color.colorMaterialBlue, R.color.colorMaterialPink)
    private val data: List<CustomBean>
        get() {
            val list = ArrayList<CustomBean>()
            for (i in introJson.indices) {
                val customBean = CustomBean()
                customBean.imageRes = introJson[i]
                customBean.backgroundRes = backgroundRes[i]
                customBean.imageDescription = des[i]
                list.add(customBean)
            }
            return list
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)



        setupViewPager()
        updateUI(0)
        btn_start?.setOnClickListener {



            viewModel.introInit()

            LoginActivity.start(this)
            finish()
        }


    }



    private fun setupViewPager() {
        mViewPager = findViewById(R.id.viewpager)
        mViewPager.apply {
            setCanLoop(false)
            setPageTransformer(PageTransformerFactory.createPageTransformer(transforms[Random().nextInt(4)]))
            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_70).toInt())
            setIndicatorSliderGap(resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_4).toInt(), resources.getDimension(R.dimen.dp_6).toInt())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {

                    updateUI(position)
                }
            })
            adapter = IntroAdapter(this@IntroActivity).apply {

            }
            setIndicatorSliderColor(ContextCompat.getColor(this@IntroActivity, R.color.white),
                    ContextCompat.getColor(this@IntroActivity, R.color.white_alpha_75))
        }.create(data)
    }

    private fun updateUI(position: Int) {
        tv_describe?.text = des[position]
        val translationAnim = ObjectAnimator.ofFloat(tv_describe, "translationX", -120f, 0f)
        translationAnim.apply {
            duration = ANIMATION_DURATION.toLong()
            interpolator = DecelerateInterpolator()
        }
        val alphaAnimator = ObjectAnimator.ofFloat(tv_describe, "alpha", 0f, 1f)
        alphaAnimator.apply {
            duration = ANIMATION_DURATION.toLong()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnim, alphaAnimator)
        animatorSet.start()

        if (position == mViewPager.data.size - 1 && btn_start?.visibility == View.GONE) {
            btn_start?.visibility = View.VISIBLE
            ObjectAnimator
                    .ofFloat(btn_start, "alpha", 0f, 1f)
                    .setDuration(ANIMATION_DURATION.toLong()).start()
        } else {
            btn_start?.visibility = View.GONE
        }
    }


    companion object {
        private const val ANIMATION_DURATION = 1300
            fun start(context: Context) {
                context.startActivity(Intent(context, IntroActivity::class.java))
            }

    }


}
