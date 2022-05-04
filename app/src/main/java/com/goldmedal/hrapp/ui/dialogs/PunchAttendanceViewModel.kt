package com.goldmedal.hrapp.ui.dialogs

import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.AttendanceRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.showPictureDialog
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class PunchAttendanceViewModel @Inject constructor(private val repository: AttendanceRepository) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null
    var imageSelectionListener: ImageSelectionListener? = null
    var strLocationAddress: String? = null
    var strComment: String? = ""
    var strBase64Image: String? = "-"
    var strDeviceId: String? = null
    var lastLocation: LatLng? = null
    var strPunchType: String? = null
    var userId: Int? = null

    fun getLoggedInUser() = repository.getUser()

    fun onPunchAttendanceButtonClick(view: View) {

        if (lastLocation?.latitude == 0.0 && lastLocation?.longitude == 0.0) {

            apiListener?.onValidationError("Unable to retrieve your location at this time,please try again", "punchIn")
            return
        } else if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "punchIn")
            return
        }else if (strDeviceId.isNullOrEmpty()) {
            apiListener?.onValidationError("Invalid deviceId", "punchIn")
            return
        }

        apiListener?.onStarted("punchIn")

        Coroutines.main {
            try {
                val punchResponse = userId?.let {
                    repository.punchAttendance(it, strComment!!, strBase64Image!!,strDeviceId!!,
                            lastLocation?.latitude.toString(), lastLocation?.longitude.toString(),strLocationAddress ?: "")
                }
                if (punchResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!punchResponse?.punchData?.isNullOrEmpty()!!) {
                        punchResponse.punchData.let {
                            apiListener?.onSuccess(it, "punchIn")

                            return@main
                        }
                    }
                } else {
                    val errorResponse = punchResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "punchIn", false) }


                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "punchIn", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "punchIn", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "punchIn", true)
            }
        }

    }


    fun onImageUploadButtonClick(view: View) {
        imageSelectionListener?.let { showPictureDialog(view.context, it) }
    }


}