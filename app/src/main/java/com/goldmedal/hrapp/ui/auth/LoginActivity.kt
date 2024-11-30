package com.goldmedal.hrapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.databinding.ActivityLoginBinding
import com.goldmedal.hrapp.ui.dashboard.DashboardActivity
import com.goldmedal.hrapp.util.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint

/**
 *
 *
 * @author Akshay Shetty
 *
 * all onClick events are defined in XML
 * please check {@link [R.layout.activity_login]}
 */

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), AuthListener<Any> {


    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = viewModel


//Make All Text UpperCase
        binding.editTextCaptcha.filters += InputFilter.AllCaps()

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@LoginActivity)

        viewModel.strGeneratedCaptcha = generateRandomCaptcha()
        binding.tvCaptcha.text = viewModel.strGeneratedCaptcha

        viewModel.getLoggedInUser().observe(this) { user ->
            if (user != null) {
                Intent(this, DashboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
        /**
         * Get FireBase Registration  Token (pushwoosh id)
         */
        // [START log_reg_token]
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                alertDialog("Restart your device and try logging in again..Please make sure you have latest version of Google Play Services installed")

                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            /**
             * Get new FCM registration token
             */
            viewModel.strFCMToken = task.result
            viewModel.saveFcmToken(task.result)
            Log.d("LoginActivity", "FCM Token: " + task.result)


        })
        // [END log_reg_token]


    }


    override fun onStarted() {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>) {
        binding.progressBar.stop()
    }

    override fun onFailure(message: String) {
        binding.progressBar.stop()
        binding.rootLayout.snackbar(message)
    }

    override fun setCaptcha(strCaptcha: String) {
        binding.tvCaptcha.text = strCaptcha
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}
