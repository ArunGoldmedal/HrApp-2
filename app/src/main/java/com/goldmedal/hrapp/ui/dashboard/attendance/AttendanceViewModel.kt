package com.goldmedal.hrapp.ui.dashboard.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.GlobalConstant.ATTENDANCE_SUMMARY
import com.goldmedal.hrapp.data.repositories.AttendanceRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.lazyDeferred
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class AttendanceViewModel @Inject constructor(
        private val repository: AttendanceRepository
) : ViewModel() {


    var userId: Int? = null
    var strStartDate: String? = null
    var strEndDate: String? = null

    // - - - - -  ge t list of all attendance till date ---- - - - - - - - - -
    val attendanceData by lazyDeferred {
        repository.getAllAttendanceData(userId, strStartDate!!, strEndDate!!)

    }


    fun getLoggedInUser() = repository.getUser()
    fun getCurrentAttendanceDataDetail() = repository.retrieveCurrentAttendanceData()

    var apiListener: ApiStageListener<Any>? = null


    // - - - - -  get team attendance details ---- - - - - - - - - -

    fun getAttendanceDetails(userId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "team_attendance")
            return
        }

        apiListener?.onStarted("team_attendance")

        Coroutines.main {
            try {
                val attendanceResponse = repository.getAttendanceDetails(userId, strStartDate!!,strEndDate!!)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.attendanceDetailsData?.isNullOrEmpty()!!) {
                        attendanceResponse.attendanceDetailsData.let {
                            apiListener?.onSuccess(it, "team_attendance")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "team_attendance", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "team_attendance", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "team_attendance", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "team_attendance", true)
            }
        }

    }


    // - - - - -  get list of all attendance till date ---- - - - - - - - - -
    fun currentAttendance() {

        apiListener?.onStarted("today_attendance")

        Coroutines.main {
            try {
                val attendanceResponse = repository.currentAttendanceData(userId!!, strEndDate!!)

                if (!attendanceResponse.currAttendanceData?.isNullOrEmpty()!!) {
                    attendanceResponse.currAttendanceData.let {
                        apiListener?.onSuccess(it, "today_attendance")
                        Log.d("Inside curr attendance", "Msg - - - -" + it.size);
                        println("inside curr attendance - - - - - -  - --  - - - - - -")
                        repository.removeCurrentAttendanceData()
                        repository.saveCurrentAttendanceData(it)
                        print("inside curr attendance - - - " + it.size)
                        return@main
                    }
                }

                apiListener?.onError(attendanceResponse.StatusCodeMessage!!, "today_attendance", false)
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "today_attendance", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "today_attendance", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "today_attendance", true)
            }
        }

    }


    /*
    * Attendance Summary Details
    **/
    fun getAttendanceSummary(userId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", ATTENDANCE_SUMMARY)
            return
        }

        apiListener?.onStarted(ATTENDANCE_SUMMARY)

        Coroutines.main {
            try {
                val attendanceResponse = repository.getAttendanceSummary(userId, strStartDate!!,strEndDate!!)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, ATTENDANCE_SUMMARY)
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, ATTENDANCE_SUMMARY, false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message ?: "", ATTENDANCE_SUMMARY, true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message ?: "", ATTENDANCE_SUMMARY, true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message?: "", ATTENDANCE_SUMMARY, true)
            }
        }

    }



    fun insertAttendanceRegularization(userId: Int?,punchDate: String?,oldPunchIn:String?,oldPunchOut:String?,newPunchIn:String?,newPunchOut:String?,
                                       reason:String,remark:String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "apply_attendance_reg")
            return
        }

        if (punchDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Punch Date cannot be nil", "apply_attendance_reg")
            return
        }
        if (oldPunchIn.isNullOrEmpty()) {
            apiListener?.onValidationError("Old Punch-In Time cannot be nil", "apply_attendance_reg")
            return
        }
        if (oldPunchOut.isNullOrEmpty()) {
            apiListener?.onValidationError("Old Punch-Out Time cannot be nil", "apply_attendance_reg")
            return
        }
        if (newPunchIn.isNullOrEmpty()) {
            apiListener?.onValidationError("New Punch-In Time cannot be nil", "apply_attendance_reg")
            return
        }
        if (newPunchOut.isNullOrEmpty()) {
            apiListener?.onValidationError("New Punch-Out Time cannot be nil", "apply_attendance_reg")
            return
        }
        if (reason == "-1") {
            apiListener?.onValidationError("Please Select Reason for Adjustment", "apply_attendance_reg")
            return
        }

        apiListener?.onStarted("apply_attendance_reg")

        Coroutines.main {
            try {
                val attendanceResponse = repository.insertAttendanceRegularization(userId, punchDate ,oldPunchIn,
                        oldPunchOut,newPunchIn,newPunchOut
                ,reason,remark)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "apply_attendance_reg")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "apply_attendance_reg", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "apply_attendance_reg", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "apply_attendance_reg", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "apply_attendance_reg", true)
            }
        }

    }

    fun getAttendanceRegularizeList(userId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "attendance_reg_list")
            return
        }

        apiListener?.onStarted("attendance_reg_list")

        Coroutines.main {
            try {
                val attendanceResponse = repository.getAttendanceRegularizeList(userId)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "attendance_reg_list")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "attendance_reg_list", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "attendance_reg_list", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "attendance_reg_list", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "attendance_reg_list", true)
            }
        }

    }

    fun getAttendanceRegularizeForApproval(userId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "attendance_reg_approval_list")
            return
        }

        apiListener?.onStarted("attendance_reg_approval_list")

        Coroutines.main {
            try {
                val attendanceResponse = repository.getAttendanceRegularizeForApproval(userId)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "attendance_reg_approval_list")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "attendance_reg_approval_list", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "attendance_reg_approval_list", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "attendance_reg_approval_list", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "attendance_reg_approval_list", true)
            }
        }

    }

    fun insertApprovedDisapprovedAttendanceReg(userId: Int?,uniqueId: Int?,approvalType:Int) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "respond_attendance_reg")
            return
        }
        if (uniqueId == null) {
            apiListener?.onValidationError("record id cannot be nil", "respond_attendance_reg")
            return
        }
        apiListener?.onStarted("respond_attendance_reg")

        Coroutines.main {
            try {
                val attendanceResponse = repository.insertApprovedDisapprovedAttendanceReg(userId, uniqueId,approvalType)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "respond_attendance_reg")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "respond_attendance_reg", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "respond_attendance_reg", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "respond_attendance_reg", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "respond_attendance_reg", true)
            }
        }

    }

    fun getAttendanceRegularizeByUser(userId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "attendance_reg_user")
            return
        }

        apiListener?.onStarted("attendance_reg_user")

        Coroutines.main {
            try {
                val attendanceResponse = repository.getAttendanceRegularizeByUser(userId)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "attendance_reg_user")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "attendance_reg_user", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "attendance_reg_user", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "attendance_reg_user", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "attendance_reg_user", true)
            }
        }

    }


    fun cancelRegularizationRequest(userId: Int?,uniqueId: Int?) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "cancel_reg_request")
            return
        }
        if (uniqueId == null) {
            apiListener?.onValidationError("record id cannot be nil", "cancel_reg_request")
            return
        }

        apiListener?.onStarted("cancel_reg_request")

        Coroutines.main {
            try {
                val attendanceResponse = repository.cancelRegularizationRequest(userId,uniqueId)
                if (attendanceResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!attendanceResponse.data?.isNullOrEmpty()!!) {
                        attendanceResponse.data.let {
                            apiListener?.onSuccess(it, "cancel_reg_request")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = attendanceResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "cancel_reg_request", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "cancel_reg_request", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "cancel_reg_request", true)

            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "cancel_reg_request", true)
            }
        }

    }


}
