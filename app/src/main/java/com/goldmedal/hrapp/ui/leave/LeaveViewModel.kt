package com.goldmedal.hrapp.ui.leave

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.LeaveRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import com.goldmedal.hrapp.util.showPictureDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class LeaveViewModel @Inject constructor(
        private val repository: LeaveRepository
) : ViewModel() {

    var apiListener: ApiStageListener<Any>? = null
    var imageSelectionListener: ImageSelectionListener? = null

    //VARIABLES


    var userId: Int? = null


    //APPLY LEAVE  - - - - - - - - - - -
    var strStartDate: String? = null
    var strEndDate: String? = null
     var strLeaveConsiderId: Int? = 0
    var strBase64Image: String? = ""
    var strActualLeaveDays: String? = "0"
    var strAppliedLeaveDays: String? = "0"
    var strLeaveReasonId: String? = "-1"
     var strDayType: String? = "Full"

    //LEAVE TYPE  - - - - - - - - - - -
    var strLeaveTypeId: String? = "1"

    // RESPOND LEAVES  - - - - - - - - - - -
    var employeeID: Int? = null
    var applyLeaveId: Int? = null
    var strRemarks: String? = ""
    var strPaidLeave: String? = "0"
    var strUnPaidLeave: String? = "0"
    var strAppliedDays: String? = null
    var leaveType: Int = 1


    fun getLeaveBalanceData() = repository.getLeaveBalanceData()
    fun getLoggedInUser() = repository.getLoggedInUser()

    fun leaveRecordData(userID: Int?,strLeaveYear: String) {


        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "leaveRecord")
            return
        }
        apiListener?.onStarted("leaveRecord")

        Coroutines.main {
            try {


                val leaveResponse = repository.getLeaveRecord(userID,strLeaveYear)
                if (leaveResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse.leaveRecordData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveRecordData.let {
                            apiListener?.onSuccess(it, "leaveRecord")
                            Log.d("LeaveRecord", "Msg - - - -" + it.size);

                            return@main
                        }
                    }
                } else {
                    val errorResponse = leaveResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leaveRecord", false) }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leaveRecord", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leaveRecord", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leaveRecord", true)
            }
        }

    }

    fun leaveTypeData() {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "leaveType")
            return
        }


        apiListener?.onStarted("leaveType")

        Coroutines.main {
            try {
                val leaveResponse = repository.getLeaveType(userId!!)
                if (leaveResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse.leaveTypeData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveTypeData.let {
                            apiListener?.onSuccess(it, "leaveType")
                            Log.d("LeaveType", "Msg - - - -" + it.size)

                            return@main
                        }
                    }
                } else {
                    val errorResponse = leaveResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leaveType", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leaveType", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leaveType", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leaveType", true)
            }
        }

    }


    fun appliedLeavesCount() {

        if (strStartDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select Start Date", "appliedLeavesCount")
            return
        } else if (strEndDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select End Date", "appliedLeavesCount")
            return
        }

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "appliedLeavesCount")
            return
        }
        apiListener?.onStarted("appliedLeavesCount")

        Coroutines.main {
            try {
                val leaveResponse = repository.getActualLeaveCount(userId!!, strStartDate!!, strEndDate!!,strLeaveConsiderId!!,strDayType!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.leaveCountData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveCountData.let {
                            apiListener?.onSuccess(it, "appliedLeavesCount")
                            Log.d("LeaveType", "Msg - - - -" + it.size);

                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "appliedLeavesCount", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "appliedLeavesCount", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "appliedLeavesCount", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "appliedLeavesCount", true)
            }
        }

    }

    fun leaveBalanceApi(year: String) {

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "leave_balance")
            return
        }
        apiListener?.onStarted("leave_balance")

        Coroutines.main {
            try {
                val leaveResponse = repository.leaveBalance(userId!!,year)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.leaveBalData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveBalData.let {
                            apiListener?.onSuccess(it, "leave_balance")
                            repository.removeLeaveBalanceData()
                            repository.saveLeaveBalance(it)
                            return@main
                        }
                    }


                } else {
                    if (leaveResponse.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeLeaveBalanceData()
                    }


                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leave_balance", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leave_balance", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leave_balance", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leave_balance", true)
            }
        }

    }

    fun leaveReasons() {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "leaveReasons")
            return
        }

        apiListener?.onStarted("leaveReasons")

        Coroutines.main {
            try {
                val leaveResponse = repository.leaveReasons(userId!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.leaveReasonsData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveReasonsData.let {
                            apiListener?.onSuccess(it, "leaveReasons")
                            Log.d("LeaveType", "Msg - - - -" + it.size)

                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leaveReasons", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leaveReasons", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leaveReasons", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leaveReasons", true)
            }
        }

    }

    fun getLeaveRequests(userID: Int?,type : Int) { //type - 1 for pending requests / 2 for approved/rejected requests


        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "leaveRequests")
            return
        }

        apiListener?.onStarted("leaveRequests")

        Coroutines.main {
            try {
                val leaveResponse = repository.getLeaveRequests(userID,type)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.leaveRequestsData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveRequestsData.let {
                            apiListener?.onSuccess(it, "leaveRequests")


                            return@main
                        }
                    }
                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leaveRequests", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leaveRequests", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leaveRequests", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leaveRequests", true)
            }
        }

    }

    fun approveLeaves() {

        when {
            userId == null -> {
                apiListener?.onValidationError("User id cannot be nil", "approveLeaves")
                return
            }
            applyLeaveId == null -> {
                apiListener?.onValidationError("Leave ID cannot be nil", "approveLeaves")
                return
            }
            strPaidLeave.isNullOrEmpty() -> {
                apiListener?.onValidationError("Please Enter Paid Leave", "approveLeaves")
                return
            }
            strUnPaidLeave.isNullOrEmpty() -> {
                apiListener?.onValidationError("Please Enter UnPaid Leave", "approveLeaves")
                return
            }
            strAppliedDays.isNullOrEmpty() -> {
                apiListener?.onValidationError("Applied days cannot be nil", "approveLeaves")
                return
            }
            employeeID == null -> {
                apiListener?.onValidationError("Employee ID cannot be nil", "approveLeaves")
                return
            }
            strRemarks.isNullOrEmpty() -> {
                strRemarks = "-"
            }
        }


        apiListener?.onStarted("approveLeaves")

        Coroutines.main {
            try {
                val leaveResponse = repository.approveLeaves(userId!!, applyLeaveId!!, strRemarks!!, leaveType, strPaidLeave!!, strUnPaidLeave!!, strAppliedDays!!, employeeID!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.respondLeavesData?.isNullOrEmpty()!!) {
                        leaveResponse.respondLeavesData.let {
                            apiListener?.onSuccess(it, "approveLeaves")


                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "approveLeaves", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "approveLeaves", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "approveLeaves", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "approveLeaves", true)
            }

        }
    }

    fun disApproveLeaves() {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "disApproveLeaves")
            return
        } else if (applyLeaveId == null) {
            apiListener?.onValidationError("Leave ID cannot be nil", "disApproveLeaves")
            return
        }else if (strRemarks.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Enter Your comment", "disApproveLeaves")
            return
        }

        apiListener?.onStarted("disApproveLeaves")

        Coroutines.main {
            try {
                val leaveResponse = repository.disApproveLeaves(userId!!, applyLeaveId!!, strRemarks!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.respondLeavesData?.isNullOrEmpty()!!) {
                        leaveResponse.respondLeavesData.let {
                            apiListener?.onSuccess(it, "disApproveLeaves")


                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "disApproveLeaves", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "disApproveLeaves", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "disApproveLeaves", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "disApproveLeaves", true)
            }

        }
    }


    fun cancelLeave(userID: Int?) {
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "leaveCancel")
            return
        } else if (applyLeaveId == null) {
            apiListener?.onValidationError("Leave ID cannot be nil", "leaveCancel")
            return
        }
        apiListener?.onStarted("leaveRespond")

        Coroutines.main {
            try {
                val leaveResponse = repository.cancelLeave(userID, applyLeaveId!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.respondLeavesData?.isNullOrEmpty()!!) {
                        leaveResponse.respondLeavesData.let {
                            apiListener?.onSuccess(it, "leaveCancel")


                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "leaveCancel", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "leaveCancel", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "leaveCancel", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "leaveCancel", true)
            }

        }
    }

    fun onImageUploadButtonClick(view: View) {
        imageSelectionListener?.let { showPictureDialog(view.context, it) }
    }


    fun onApplyLeavesButtonClick(view: View) {


        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "applyLeave")
            return
        }
        if (strStartDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select Start Date", "applyLeave")
            return
        } else if (strEndDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select End Date", "applyLeave")
            return
        } else if (strLeaveReasonId == "-1") {
            apiListener?.onValidationError("Please Select Leave Reason", "applyLeave")
            return
        } else if (strActualLeaveDays?.toDouble() ?: 0.0 <= 0.0) {
            apiListener?.onValidationError("Actual Leave Days cannot be less than or equal to zero", "applyLeave")
            return
        }

        apiListener?.onStarted("applyLeave")

        Coroutines.main {
            try {
                val leaveResponse = repository.applyLeave(userId!!, strLeaveTypeId!!, strStartDate!!, strEndDate!!, strBase64Image!!, strLeaveReasonId!!, strAppliedLeaveDays!!, strActualLeaveDays!!,strLeaveConsiderId!!, strDayType!!)
                if (leaveResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!leaveResponse?.leaveApplyData?.isNullOrEmpty()!!) {
                        leaveResponse.leaveApplyData.let {
                            apiListener?.onSuccess(it, "applyLeave")


                            return@main
                        }
                    }


                } else {
                    val errorResponse = leaveResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "applyLeave", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "applyLeave", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "applyLeave", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "applyLeave", true)
            }
        }
    }

    fun applyOD(subLocationId:Int){

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "applyOD")
            return
        } else if (strStartDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select Start Date", "applyOD")
            return
        } else if (strEndDate.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Select End Date", "applyOD")
            return
        } else if (subLocationId == 0) {
            apiListener?.onValidationError("Please Select Outdoor Location", "applyOD")
            return
        }

        else if (strRemarks.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Enter Your remarks", "applyOD")
            return
        }


        apiListener?.onStarted("applyOD")
        Coroutines.main {
            try {
                val response = repository.applyOD(userId!!,strStartDate!!,strEndDate!!, subLocationId, strRemarks!!)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "applyOD")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "applyOD", false) }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "applyOD", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "applyOD", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "applyOD", true)
            }
        }
    }


    fun applySL(date: String,noOfHours:String){

        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "applySL")
            return
        } else if (date.isEmpty()) {
            apiListener?.onValidationError("Please Select Date", "applySL")
            return
        }

        else if (noOfHours == "Select") {
            apiListener?.onValidationError("Please Select No. of Hours", "applySL")
            return
        }

        else if (strRemarks.isNullOrEmpty()) {
            apiListener?.onValidationError("Please Enter Your remarks", "applySL")
            return
        }


        apiListener?.onStarted("applySL")
        Coroutines.main {
            try {
                val response = repository.applySL(userId!!,date,noOfHours, strRemarks!!)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "applySL")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "applySL", false) }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message ?: "", "applySL", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message?: "", "applySL", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message?: "", "applySL", true)
            }
        }
    }


    fun getSubLocationList() {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "subLocationList")
            return
        }
        apiListener?.onStarted("subLocationList")
        Coroutines.main {
            try {
                val response = repository.getSubLocationList(userId!!)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "subLocationList")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "subLocationList", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "subLocationList", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "subLocationList", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "subLocationList", true)
            }
        }

    }

    fun getODList(userID: Int?) {
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "ODList")
            return
        }
        apiListener?.onStarted("ODList")
        Coroutines.main {
            try {
                val response = repository.getODList(userID)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "ODList")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "ODList", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "ODList", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "ODList", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "ODList", true)
            }
        }

    }


    fun getSLHistory(userID: Int?) {
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "SLHistory")
            return
        }
        apiListener?.onStarted("SLHistory")
        Coroutines.main {
            try {
                val response = repository.getSLHistory(userID)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "SLHistory")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "SLHistory", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "SLHistory", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "SLHistory", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "SLHistory", true)
            }
        }

    }

    fun getODListForApproval(userID: Int?) {
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "ODListForApproval")
            return
        }
        apiListener?.onStarted("ODListForApproval")
        Coroutines.main {
            try {
                val response = repository.getODListForApproval(userID)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "ODListForApproval")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "ODListForApproval", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "ODListForApproval", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "ODListForApproval", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "ODListForApproval", true)
            }
        }

    }


    fun getShortLeaveForApproval(userID: Int?) {
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "SLForApproval")
            return
        }
        apiListener?.onStarted("SLForApproval")
        Coroutines.main {
            try {
                val response = repository.getShortLeaveForApproval(userID)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "SLForApproval")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "SLForApproval", false) }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "SLForApproval", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "SLForApproval", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "SLForApproval", true)
            }
        }

    }


    fun approveRejectOD(userID: Int?,requestId:Int?,type: Int) { //Type - 1 for Approve / 2 for Disapprove
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "approveRejectOD")
            return
        }
        apiListener?.onStarted("approveRejectOD")
        Coroutines.main {
            try {
                val response = repository.approveRejectOD(userID,requestId ?: 0,type)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "approveRejectOD")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "approveRejectOD", false) }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "approveRejectOD", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "approveRejectOD", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "approveRejectOD", true)
            }
        }

    }


    fun approveRejectSL(userID: Int?,requestId:Int?,type: Int) { //Type - 1 for Approve / 2 for Disapprove
        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "approveRejectSL")
            return
        }
        apiListener?.onStarted("approveRejectSL")
        Coroutines.main {
            try {
                val response = repository.approveRejectSL(userID,requestId ?: 0,type)
                if (response.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!response.data?.isNullOrEmpty()!!) {
                        response.data.let {
                            apiListener?.onSuccess(it, "approveRejectSL")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "approveRejectSL", false) }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "approveRejectSL", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "approveRejectSL", true)
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "approveRejectSL", true)
            }
        }

    }






























}