package com.goldmedal.hrapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.ResetPasswordData
import com.goldmedal.hrapp.databinding.ActivityForgotPasswordBinding
import com.goldmedal.hrapp.util.alertDialog
import com.goldmedal.hrapp.util.hideKeyboard
import com.goldmedal.hrapp.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity(), AuthListener<Any> {
    private var isFromProfile: Boolean = false
    private  val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        binding.viewmodelforgot = viewModel
        viewModel.authListener = this


        isFromProfile = intent.getBooleanExtra("changePasswordFromProfile", false)
        viewModel.strMobileNo = intent.getStringExtra("MobileNo")

        if (isFromProfile) {
            binding.viewOldPassword.visibility = View.VISIBLE

        }else{
            viewModel.strOldPassword = "-"
        }
    }

    override fun onStarted() {

        hideKeyboard()
        binding.progressBar.start()

    }

    override fun onSuccess(_object: List<Any?>) {
        binding.progressBar.stop()


        //Save password and logout of all active signed in devices
        viewModel.saveUserPassword(viewModel.strNewPassword)


        val data = _object as List<ResetPasswordData>
        toast(data[0].Status ?: "")
        if (isFromProfile) {
            finish()
        } else {
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    override fun onFailure(message: String) {
        binding.progressBar.stop()
        alertDialog(message)
    }

    override fun setCaptcha(strCaptcha: String) {

    }
}
