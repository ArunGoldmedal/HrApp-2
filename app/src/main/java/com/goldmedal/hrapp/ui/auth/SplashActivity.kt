package com.goldmedal.hrapp.ui.auth

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.User
import com.goldmedal.hrapp.data.model.InitialApiData
import com.goldmedal.hrapp.ui.dashboard.DashboardActivity
import com.goldmedal.hrapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity(), AuthListener<Any> {


    private val viewModel: LoginViewModel by viewModels()

    private val splashJson = arrayOf(R.raw.splash_1, R.raw.splash_2, R.raw.splash_3)
    private var initialApiCalled = false
    private var isVideoCompleted = false
    private var initialApiSuccess = false
    private var isActive: Boolean? = true
    private var forceUpdate: Boolean? = false
    private var playStoreVersionCode: Int = 0

    private var strUserPasswordOnServer: String? = ""

    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.authListener = this

        startApi()
    }

    override fun onResume() {
        super.onResume()
        lottie?.setAnimation(splashJson.random())
        lottie?.playAnimation()

        lottie?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isVideoCompleted = true
                if (!initialApiCalled) {
                    startApi()
                }

                if (initialApiSuccess) {
                    userAccountStatus()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    private fun startApi() {
        viewModel.getLoggedInUser().observe(this@SplashActivity, Observer { user ->
            this.user = user
            if (!initialApiSuccess) {
                viewModel.initialApi(userId = user?.UserID ?: 0)
            } else {
                LoginActivity.start(this@SplashActivity)
                finish()
            }

        })
        initialApiCalled = true
    }


    private fun comparePassword() {
val savedPasswordOnDevice = viewModel.getUserPassword()

        //Password was not found re-direct to Login screen
        if(savedPasswordOnDevice == null){
            viewModel.logoutUser()
        }else{
            if (strUserPasswordOnServer == savedPasswordOnDevice) {
                //Account is Active,Proceed
                Intent(this@SplashActivity, DashboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }else{
                //User has been compromised
                viewModel.logoutUser()
            }
        }

    }

    private fun userAccountStatus() {
        val isIntroScreenShown = viewModel.isIntroInit()
        if (isIntroScreenShown) {
            if (user != null) {
                if (isActive == true) {
                        //Check If Password was changed
                            comparePassword()
                } else {
                    //Impostor was Ejected
                    toast("${user?.FirstName}, Your Account has been Disabled.")
                    viewModel.logoutUser()
                }
            } else {
                LoginActivity.start(this@SplashActivity)
                finish()
            }
        } else {
            IntroActivity.start(this@SplashActivity)
            finish()
        }
    }

    override fun onStarted() {}

    override fun onSuccess(_object: List<Any?>) {
        val data = _object as List<InitialApiData>?
        isActive = data?.get(0)?.isActive
        strUserPasswordOnServer = data?.get(0)?.Password ?: ""
        forceUpdate = data?.get(0)?.forceUpdate
        playStoreVersionCode = data?.get(0)?.VersionCode?.toInt() ?: 0
        val versionName = data?.get(0)?.VersionName



        viewModel.saveInitialData(playStoreVersionCode, versionName, isActive ?: true, forceUpdate
                ?: false)
        if (isVideoCompleted) {
            userAccountStatus()
        }
        initialApiSuccess = true
    }

    override fun onFailure(message: String) {
        userAccountStatus()
        toast(message)
    }

    override fun setCaptcha(strCaptcha: String) {}

    companion object {
        private const val TAG = "SplashActivity"

    }
}
