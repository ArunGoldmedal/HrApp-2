package com.goldmedal.hrapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.databinding.OtpLayoutBinding
import com.goldmedal.hrapp.util.getDeviceId
import com.goldmedal.hrapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.otp_layout.*

@AndroidEntryPoint
class VerifyOTPActivity : AppCompatActivity(), AuthListener<Any>{



private  val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: OtpLayoutBinding = DataBindingUtil.setContentView(this, R.layout.otp_layout)

        binding.viewmodel = viewModel


        viewModel.strMobileNo = intent.getStringExtra("MobileNo")
        tvMobileNumber.text = viewModel.strMobileNo

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@VerifyOTPActivity)
    }

    override fun onStarted() {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>) {

        progress_bar?.stop()



        Intent(this, ForgotPasswordActivity::class.java)
                .also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    it.putExtra("MobileNo", viewModel.strMobileNo)
                    startActivity(it)
                }
    }

    override fun onFailure(message: String) {
        progress_bar?.stop()
        root_layout?.snackbar(message)
    }

    override fun setCaptcha(strCaptcha: String) {

    }
}
