package com.goldmedal.hrapp.data.repositories

import android.os.Build
import com.goldmedal.hrapp.BuildConfig
import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.db.entities.ResetPasswordResponse
import com.goldmedal.hrapp.data.db.entities.SendOtpResponse
import com.goldmedal.hrapp.data.db.entities.User
import com.goldmedal.hrapp.data.db.entities.UserDataUpdate
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.*
import com.goldmedal.hrapp.data.preferences.PreferenceProvider
import com.goldmedal.hrapp.util.getIPAddress
import javax.inject.Inject


class UserRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase,
        private val prefs: PreferenceProvider
) : SafeApiRequest() {

    suspend fun userLogin(strUserName: String, strPassword: String,strDeviceId: String,strFCMToken: String): LoginResponse {
        return apiRequest {
            api.userLogin(strUserName,
                    strPassword,
                    strDeviceId,
                    "ANDROID",
                    BuildConfig.VERSION_NAME,
                    Build.VERSION.SDK_INT.toString(),
                    strFCMToken,
                    getIPAddress(true),
                    GlobalConstant.CLIENT_ID,
                    "strLat", //No need to get user location for user privacy
                    "strLong",
                    Build.MANUFACTURER + " - " + Build.MODEL,
                    GlobalConstant.CLIENT_SECRET)
        }

    }


    suspend fun updateProfilePic(userId: Int, strProfileString: String): ProfilePicResponse {
        return apiRequest { api.uploadProfilePic(userId, strProfileString) }
    }

    suspend fun editProfile(userId: Int, strFatherName: String,strMotherName: String,strSpouseName:String,strAnniversaryDate:String,strFatherDOB:String,
            strMotherDOB:String,strSpouseDOB:String,strPersonalEmail:String,strOfficeEmail:String,strMobileNo:String,strHomeAddress:String,
           maritalStatus:Int,strChildName1:String,strChildName2:String,strChildName3:String,
            strChildDOB1:String,strChildDOB2:String,strChildDOB3:String
                            ): UpdateProfileResponse {
        return apiRequest { api.updateUserProfile(userId = userId, fatherName = strFatherName,motherName = strMotherName,spouseName = strSpouseName,
        anniversaryDate = strAnniversaryDate,fatherDOB = strFatherDOB,motherDOB = strMotherDOB,spouseDOB = strSpouseDOB,personalEmail = strPersonalEmail,officeEmail = strOfficeEmail,
                mobileNo = strMobileNo,martialStatus = maritalStatus,childName1 = strChildName1,childName2 = strChildName2,
                childName3 = strChildName3,childDOB1 = strChildDOB1,childDOB2 = strChildDOB2,childDOB3 = strChildDOB3
                ) }//homeAddress = strHomeAddress,
    }


    suspend fun userGetOtp(strMobileNo: String,strDeviceId: String): SendOtpResponse {
        return apiRequest { api.sendOtp(strMobileNo, strDeviceId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }

    }

    suspend fun userSendOtp(strMobileNo: String, strOtp: String, strRequestNo: String,strDeviceId: String): VerifyOtpResponse {
        return apiRequest { api.verifyOtp(strMobileNo, strOtp, strRequestNo, strDeviceId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }

    }

    suspend fun userResetPassword(strMobileNo: String, strNewPassword: String, strOldPassword: String): ResetPasswordResponse {
        return apiRequest { api.resetPassword(strMobileNo, strNewPassword, strOldPassword, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }

    }


    suspend fun initialApi(userId: Int): InitialApiResponse {
        return apiRequest { api.initialApi(userId,GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }

    }

    suspend fun saveProfilePic(profilePicLink: String?) = db.getUserDao().updateProfilePic(profilePicLink)


    suspend fun saveUser(user: User?) = db.getUserDao().upsert(user)
    suspend fun updateUserProfile(user: UserDataUpdate?) = db.getUserDao().update(user)

    fun getUser() = db.getUserDao().getUser()

    fun saveUserPassword(password: String?) = prefs.saveUserPassword(password)
    fun getUserPassword() = prefs.getUserPassword()


    suspend fun logoutUser() = db.getUserDao().logoutUser()
    fun clearUserCache() = prefs.saveLastSavedAt(null)
    fun clearUserPassword() = prefs.saveUserPassword(null)

    fun introInit() = prefs.introInit(true)
    fun isIntroInit() = prefs.isIntroInit()

    fun saveInitialData(verCode: Int,verName: String?,isActive: Boolean,forceUpdate: Boolean) =
            prefs.saveInitialData(verCode, verName, isActive, forceUpdate)

    fun getVersionCode() = prefs.getVersionCode()
    fun getForceUpdateFlag() = prefs.getForceUpdateFlag()

    fun saveFCMToken(token: String?) = prefs.saveFCMToken(token)
    fun getFCMToken() = prefs.getFCMToken()
}