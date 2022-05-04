package com.goldmedal.hrapp.ui.dashboard.profile

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.UserRepository
import com.goldmedal.hrapp.ui.auth.ForgotPasswordActivity
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.showPictureDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
        private val repository: UserRepository
) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null


    var strMobileNo: String? = null

    var strBase64Image: String? = "-"

    var imageSelectionListener: ImageSelectionListener? = null


    //Edit Profile - - - - - - - - - - - -

    var strFatherDOB: String? = ""
    var strMotherDOB: String? = ""
    var strSpouseDOB: String? = ""
    var strAnniversaryDate: String? = ""
    var strChild1DOB: String? = ""
    var strChild2DOB: String? = ""
    var strChild3DOB: String? = ""



    fun getLoggedInUser() = repository.getUser()



    fun onChangePasswordButtonClick(view: View) {

        Intent(view.context, ForgotPasswordActivity::class.java)
                .putExtra("changePasswordFromProfile",true)
                .putExtra("MobileNo",strMobileNo)
                .also {
            view.context.startActivity(it)
        }
    }

    fun onProfileUploadButtonClick(view: View) {
        imageSelectionListener?.let { showPictureDialog(view.context, it) }
    }


    fun updateProfilePic(userId: Int?) {

        apiListener?.onStarted("updateProfile")

        Coroutines.main {
            try {
                val profilePicResponse = userId?.let { repository.updateProfilePic(it,strBase64Image!!) }
                if (profilePicResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!profilePicResponse?.getProfileLink?.isNullOrEmpty()!!) {
                        profilePicResponse.getProfileLink.let {
                            repository.saveProfilePic(it[0].ProfilePhoto ?: "") //https://goldblobtest.blob.core.windows.net/goldappdata/goldapp/base/hrm/employeedocuments/pranitgharat-982d2bad-b114-3911-3a4f-1a26507919b6.png
                            apiListener?.onSuccess(it, "updateProfile")
                            return@main
                        }
                    }
                }else {
                    val errorResponse = profilePicResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "updateProfile", false) }


                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "updateProfile", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "updateProfile", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "updateProfile", true)
            }


        }

    }


    fun editProfile(userId: Int?, strFatherName: String,strMotherName: String,strSpouseName:String,
                    strPersonalEmail:String,strOfficeEmail:String,strMobileNo:String,strHomeAddress:String,
                    maritalStatus:Int,strChildName1:String,strChildName2:String,strChildName3:String) {


        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "editProfile")
            return
        }

        apiListener?.onStarted("editProfile")

        Coroutines.main {
            try {
                val outputResponse =  repository.editProfile(userId, strFatherName = strFatherName,strMotherName = strMotherName,strSpouseName = strSpouseName,
                        strAnniversaryDate = strAnniversaryDate ?: "",strFatherDOB = strFatherDOB ?: "",strMotherDOB = strMotherDOB ?: "",strSpouseDOB = strSpouseDOB ?: "",strPersonalEmail = strPersonalEmail,
                        strMobileNo = strMobileNo,strHomeAddress = strHomeAddress,maritalStatus = maritalStatus,strChildName1 = strChildName1,strChildName2 = strChildName2,
                        strOfficeEmail = strOfficeEmail,strChildName3 = strChildName3,strChildDOB1 = strChild1DOB ?: "",strChildDOB2 = strChild2DOB ?: "" ,strChildDOB3 = strChild3DOB ?: "")
                if (outputResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!outputResponse?.user?.isNullOrEmpty()!!) {
                        outputResponse.user.let {
                            repository.updateUserProfile(it[0])
                            apiListener?.onSuccess(it, "editProfile")
                            return@main
                        }
                    }
                }else {
                    val errorResponse = outputResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "editProfile", false) }


                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "editProfile", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "editProfile", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "editProfile", true)
            }


        }

    }
}
