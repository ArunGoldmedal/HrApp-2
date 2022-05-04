package com.goldmedal.hrapp.ui.dialogs

import android.util.Log
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.AttendanceHistoryRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class AttendanceHistoryViewModel @Inject constructor(
        private val repository: AttendanceHistoryRepository
) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null
    var userId: Int? = null
    var attendanceStatus: Int =  2  // 1 - ALL
                                    // 2 - PRESENT
                                    // 3 - ABSENT



    fun getLoggedInUser() = repository.getUser()

fun getAttendanceHistory() {

    if (userId == null) {
        apiListener?.onValidationError("User id cannot be nil", "attendance_history")
        return
    }


    apiListener?.onStarted("attendance_history")

    Coroutines.main {
        try {
            val attendanceHistoryResponse = repository.getAttendanceHistory(userId!!,attendanceStatus )
            if (attendanceHistoryResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                if (!attendanceHistoryResponse?.attendanceHistoryData?.isNullOrEmpty()!!) {
                    attendanceHistoryResponse.attendanceHistoryData.let {
                        apiListener?.onSuccess(it, "attendance_history")
                        Log.d("LeaveType", "Msg - - - -" + it.size);

                        return@main
                    }
                }


            } else {
                val errorResponse = attendanceHistoryResponse?.Errors
                if (!errorResponse?.isNullOrEmpty()!!) {
                    errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "attendance_history", false) }
                }
            }

        } catch (e: ApiException) {
            apiListener?.onError(e.message!!, "attendance_history", true)
        } catch (e: NoInternetException) {

            apiListener?.onError(e.message!!, "attendance_history", true)
            print("Internet not available")
        }catch (e: SocketTimeoutException) {
            apiListener?.onError(e.message!!, "attendance_history", true)
        }
    }

}


}