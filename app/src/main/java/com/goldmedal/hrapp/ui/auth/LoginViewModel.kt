package com.goldmedal.hrapp.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.data.db.entities.User
import com.goldmedal.hrapp.data.network.GlobalConstant.SUCCESS_CODE
import com.goldmedal.hrapp.data.repositories.UserRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.generateRandomCaptcha
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
        private val repository: UserRepository
) : ViewModel() {

    var strUserName: String? = null
    var strDeviceId: String? = null
    var strFCMToken: String? = null
    var strMobileNo: String? = null
    var strRequestNo: String? = null
    var strOtp: String? = null
    var strPassword: String? = null
    var strNewPassword: String? = null
    var strConfirmPassword: String? = null
    var strOldPassword: String? = null
    var strCaptcha: String? = null
    var strGeneratedCaptcha: String? = null


    private val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,})"

    var authListener: AuthListener<Any>? = null

    fun saveUserPassword(password: String?) = repository.saveUserPassword(password)
    fun getUserPassword() = repository.getUserPassword()


    fun getLoggedInUser() = repository.getUser()

    fun introInit() = repository.introInit()
    fun isIntroInit() = repository.isIntroInit()

    fun saveInitialData(verCode: Int, verName: String?, isActive: Boolean, forceUpdate: Boolean) = repository.saveInitialData(verCode, verName, isActive, forceUpdate)



    fun getVersionCode() = repository.getVersionCode()
    fun getForceUpdateFlag() = repository.getForceUpdateFlag()

    fun logoutUser() =

            Coroutines.main {
                repository.logoutUser()
                repository.clearUserCache()
                repository.clearUserPassword()
            }

    fun onCaptchaReload(view: View) {
        strGeneratedCaptcha = generateRandomCaptcha()
        strGeneratedCaptcha?.let { authListener?.setCaptcha(it) }
        print("Captcha - - - " + strGeneratedCaptcha + "- - -  -" + strCaptcha)

    }

    fun onLoginButtonClick(view: View) {
        if (strUserName.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email / mobile no / employee code")
            return
        } else if (strPassword.isNullOrEmpty()) {
            authListener?.onFailure("Invalid password")
            return
        } else if (!strCaptcha.equals(strGeneratedCaptcha)) {
            authListener?.onFailure("Invalid Captcha")
            return
        }else if (strDeviceId.isNullOrEmpty()) {
            authListener?.onFailure("Invalid deviceId")
            return
        }
        else if (strFCMToken.isNullOrEmpty()) {
            authListener?.onFailure("Invalid token")
            return
        }
        authListener?.onStarted()

        print("Captcha - - - " + strGeneratedCaptcha + "- - -  -" + strCaptcha)



        strGeneratedCaptcha?.let { authListener?.setCaptcha(it) }

        Coroutines.main {
            try {
                val loginResponse = repository.userLogin(strUserName!!, strPassword!!, strDeviceId!!, strFCMToken!!)
                if (loginResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!loginResponse.user?.isNullOrEmpty()!!) {
                        loginResponse.user.let {
                            authListener?.onSuccess(it)
                            repository.saveUser(it[0])
                            repository.saveUserPassword(strPassword)
                            return@main
                        }
                    } else {
                        authListener?.onFailure(loginResponse.StatusCodeMessage!!)
                    }
                } else {
                    val errorResponse = loginResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { authListener?.onFailure(it) }

                    }
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }catch (e: SocketTimeoutException) {
                authListener?.onFailure(e.message!!)
            }
        }

    }


    fun onForgotPassword(view: View) {
        Intent(view.context, InputDetailsActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun getOtpDetails(view: View) {

        if (strDeviceId.isNullOrEmpty()) {
            authListener?.onFailure("Invalid Device id")
            return
        }

       else if (strMobileNo.isNullOrEmpty()) {
            authListener?.onFailure("Invalid mobile no.")
            return
        } else if (!strCaptcha.equals(strGeneratedCaptcha)) {
            authListener?.onFailure("Invalid Captcha")
            return
        }

        authListener?.onStarted()

        Coroutines.main {
            try {
                val otpResponse = repository.userGetOtp(strMobileNo!!, strDeviceId!!)
                if (otpResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!otpResponse.getOtpMain?.isNullOrEmpty()!!) {
                        otpResponse.getOtpMain.let {
                            authListener?.onSuccess(it)

                            return@main
                        }
                    } else {
                        authListener?.onFailure(otpResponse.StatusCodeMessage!!)
                    }
                } else {
                    val errorResponse = otpResponse.Errors

                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { authListener?.onFailure(it) }

                    }

                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }

    fun onVerifyOtpClick(view: View) {
        if (strMobileNo.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or mobile no.")
            return
        } else if (strOtp.isNullOrEmpty()) {
            authListener?.onFailure("enter otp")
            return
        } else if (strRequestNo.isNullOrEmpty()) {
            authListener?.onFailure("enter request no")
            return
        }else if (strDeviceId.isNullOrEmpty()) {
            authListener?.onFailure("Invalid Device Id")
            return
        }



        authListener?.onStarted()

        Coroutines.main {
            try {
                val otpResponse = repository.userSendOtp(strMobileNo!!, strOtp!!, strRequestNo!!, strDeviceId!!)

                if (otpResponse.StatusCode.equals(SUCCESS_CODE)) {

                    if (!otpResponse.verifyOtpMain?.isNullOrEmpty()!!) {
                        otpResponse.verifyOtpMain.let {
                            authListener?.onSuccess(it)

                            return@main
                        }
                    } else {
                        authListener?.onFailure(otpResponse.StatusCodeMessage!!)
                    }
                }else{
                    val errorResponse = otpResponse.Errors

                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { authListener?.onFailure(it) }

                    }

                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }

    fun onResetPassword(view: View) {
        if (strMobileNo.isNullOrEmpty()) {
            authListener?.onFailure("Invalid email or mobile no.")
            return
        }
       else if (strOldPassword.isNullOrEmpty()) {
            authListener?.onFailure("enter old password")
            return
        } else if (strNewPassword.isNullOrEmpty()) {
            authListener?.onFailure("enter password")
            return
        } else if (strConfirmPassword.isNullOrEmpty()) {
            authListener?.onFailure("enter confirm password")
            return
        }else if (strConfirmPassword.isNullOrEmpty()) {
            authListener?.onFailure("enter confirm password")
            return
        }

        else if (!strNewPassword!!.matches(PASSWORD_PATTERN.toRegex())) {
            authListener?.onFailure("Password must be minimum 8 characters and should contains at least ONE CAPITAL, SMALL LETTER, ONE DIGIT,& ONE SPECIAL CHARACTER")
            return

        }
        else if (!strConfirmPassword.equals(strNewPassword)) {
            authListener?.onFailure("new and confirm password does not match")
            return
        }

        authListener?.onStarted()




        Coroutines.main {
            try {
                val resetPasswordResponse = repository.userResetPassword((strMobileNo
                        ?: ""), (strNewPassword ?: ""), strOldPassword
                        ?: "-")
                if (resetPasswordResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!resetPasswordResponse.resetPasswordMain?.isNullOrEmpty()!!) {
                        resetPasswordResponse.resetPasswordMain.let {
                            authListener?.onSuccess(it)


                            return@main
                        }
                    }
                } else {
                    val errorResponse = resetPasswordResponse.Errors

                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { authListener?.onFailure(it) }

                    }


                }

            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }

        }
    }


    fun initialApi(userId: Int) {
        authListener?.onStarted()

        Coroutines.main {
            try {
                val leaveResponse = repository.initialApi(userId)
                if (leaveResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!leaveResponse.data?.isNullOrEmpty()!!) {
                        leaveResponse.data.let {
                            authListener?.onSuccess(it)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = leaveResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { authListener?.onFailure(it) }
                    }
                }

            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!)
            }catch (e: SocketTimeoutException) {
                authListener?.onFailure(e.message!!)
            }
        }
    }

}