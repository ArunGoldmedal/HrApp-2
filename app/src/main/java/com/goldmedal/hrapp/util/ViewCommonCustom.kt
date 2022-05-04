package com.goldmedal.hrapp.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.RotateLoading


class ViewCommonCustom @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var rlMain: RelativeLayout? = null
    private var progressBar: RotateLoading? = null
    private var lottieNoInternet: LottieAnimationView? = null
//    private var imvServerError: ImageView? = null
    private var imvNoData: ImageView? = null
    private var lottieNoData: LottieAnimationView? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.common_view, this, true)

        rlMain = findViewById(R.id.rlMain)
        progressBar = findViewById(R.id.progress_bar)
        imvNoData = findViewById(R.id.imv_no_data)
        lottieNoData = findViewById(R.id.lottie_no_data)
//        imvServerError = findViewById(R.id.imv_server_error)
//        imvNoInternet = findViewById(R.id.imv_no_internet) as ImageView
        lottieNoInternet = findViewById(R.id.lottie_server_error)
    }


    fun showProgressBar() {

        progressBar?.start()
        imvNoData?.visibility = View.GONE
        lottieNoData?.visibility = View.GONE
//        imvServerError?.visibility = View.GONE
        lottieNoInternet?.visibility = View.GONE
    }

    fun showNoDataImage() {
        progressBar?.stop()

        lottieNoData?.visibility = View.GONE
        lottieNoInternet?.visibility = View.GONE
        imvNoData?.visibility = View.VISIBLE



//        imvServerError?.visibility = View.GONE
//        imvNoInternet?.visibility = View.GONE


    }


    fun showNoData() {
        progressBar?.stop()
        lottieNoInternet?.visibility = View.GONE
        imvNoData?.visibility = View.GONE
        lottieNoData?.visibility = View.VISIBLE

        lottieNoData?.setAnimation(R.raw.no_data_dog)
        lottieNoData?.playAnimation()
//        imvServerError?.visibility = View.GONE

    }

    fun showServerError() {
//        imvServerError?.visibility = View.VISIBLE
//        progressBar?.visibility = View.GONE
        progressBar?.stop()
        imvNoData?.visibility = View.GONE
        lottieNoInternet?.visibility = View.GONE
        lottieNoData?.visibility = View.GONE
    }

    fun showNoInternet() {
        progressBar?.stop()
        lottieNoInternet?.visibility = View.VISIBLE
        lottieNoInternet?.setAnimation(R.raw.no_connection)
        lottieNoInternet?.playAnimation()
//        progressBar?.visibility = View.GONE

        imvNoData?.visibility = View.GONE
        //imvServerError?.visibility = View.GONE
    }

    fun hide() {
//        progressBar?.visibility = View.GONE

        progressBar?.stop()
        lottieNoData?.cancelAnimation()
        lottieNoInternet?.cancelAnimation()

      imvNoData?.visibility = View.GONE
        lottieNoData?.visibility = View.GONE
        //imvServerError?.visibility = View.GONE
        lottieNoInternet?.visibility = View.GONE
    }

}