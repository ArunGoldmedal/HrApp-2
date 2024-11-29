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

@AndroidEntryPoint
class VerifyOTPActivity : AppCompatActivity(), AuthListener<Any>{
    private  val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: OtpLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.otp_layout)
        binding.viewmodel = viewModel

        viewModel.strMobileNo = intent.getStringExtra("MobileNo")
        binding.tvMobileNumber.text = viewModel.strMobileNo

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@VerifyOTPActivity)
    }

    override fun onStarted() {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>) {
        binding.progressBar.stop()

        Intent(this, ForgotPasswordActivity::class.java)
                .also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    it.putExtra("MobileNo", viewModel.strMobileNo)
                    startActivity(it)
                }
    }

    override fun onFailure(message: String) {
        binding.progressBar.stop()
        binding.rootLayout.snackbar(message)
    }

    override fun setCaptcha(strCaptcha: String) {}
}
