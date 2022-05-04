 package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData
import com.goldmedal.hrapp.data.model.DefaultMessageData
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.*
import javax.inject.Inject


 interface ILeaveListener {
    fun observeRequests(removeAt: Int?)

}


class LeaveRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase




) : SafeApiRequest() {


    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()

    // - - - - - -  API for getting list of leave records - - - - - - - - - - -
    suspend fun getLeaveRecord(userId: Int,strLeaveYear: String): LeaveRecordResponse {
        return apiRequest { api.getLeaveRecordDetail(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET,strLeaveYear) }
    }

    // - - - - - -  API for getting list of leave types - - - - - - - - - - -
    suspend fun getLeaveType(userId: Int): LeaveTypeResponse {
        return apiRequest { api.getLeaveTypeDetail(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - -  API for getting list of leave requests by team - - - - - - - - - - -
    suspend fun getLeaveRequests(userId: Int,type : Int): LeaveRequestsResponse {
        return apiRequest { api.leaveRequests(userId,type, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - - - - - -  API for Leave Approval - - - - - - - - - - -
    suspend fun approveLeaves(userId: Int,applyLeaveId:Int,strApprovedRemarks:String,leaveType:Int,strPaidLeave:String,strUnPaidLeave:String,strAppliedDays:String,employeeID:Int): RespondLeavesResponse {
        return apiRequest { api.approveLeaves(userId,applyLeaveId,strApprovedRemarks,leaveType,strPaidLeave,strUnPaidLeave,strAppliedDays,employeeID, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - -  API for Leave Dis-Approval - - - - - - - - - - -
    suspend fun disApproveLeaves(userId: Int,applyLeaveId:Int,strDisApproveRemarks: String): RespondLeavesResponse {
        return apiRequest { api.disApproveLeaves(userId,applyLeaveId,strDisApproveRemarks, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - -  API for Cancel Leave - - - - - - - - - - -
    suspend fun cancelLeave(userId: Int,applyLeaveId:Int): RespondLeavesResponse {
        return apiRequest { api.cancelLeave(userId,applyLeaveId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }



    // - - - - - -  API for getting applied and actual leave count based on selected days - - - - - - - - - - -
    suspend fun getActualLeaveCount(userId: Int, startDate: String, endDate: String, leaveConsiderId: Int, strDayType: String): AppliedLeaveCountResponse {
        return apiRequest { api.getAppliedLeavesCount(userId,startDate, endDate,leaveConsiderId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    suspend fun applyLeave(userId: Int, strLeaveTypeID: String, strStartDate: String, strEndDate: String, strLeaveImage: String,
                           strLeaveReasonID: String, strAppliedLeavesDay: String, strActualLeavesDay: String,leaveConsiderId: Int, strDayType: String): LeaveApplyResponse
    {
        return apiRequest { api.applyLeave(userId,strLeaveTypeID,strStartDate,strEndDate,strLeaveImage,strLeaveReasonID,strAppliedLeavesDay,strActualLeavesDay,leaveConsiderId,strDayType, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - -  - - - - - - -  GET LEAVE REASONS WHILE APPLYING LEAVES - - - - - - - -


    suspend fun leaveReasons(userId: Int): LeaveReasonsResponse {
        return apiRequest { api.leaveReasons(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - -  - - - - - - -  GET LEAVE BALANCE - - - - - - - -

    suspend fun leaveBalance(userId: Int,year:String): LeaveBalanceResponse {
        return apiRequest { api.getLeaveBalance(userId,year, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    suspend fun saveLeaveBalance(leaveBalData: List<LeaveBalanceData?>) = db.getLeaveBalanceDao().insertLeaveBalanceData(leaveBalData)

    fun getLeaveBalanceData() = db.getLeaveBalanceDao().getLeaveBalanceData()

    suspend fun removeLeaveBalanceData() = db.getLeaveBalanceDao().removeLeaveBalanceData()




    // - - - - - -  API for APPLYING OD - - - - - - - - - - -
    suspend fun applyOD(userId: Int,strStartDate: String,strEndDate: String, subLocationId: Int,strRemarks: String): DefaultMessageResponse {
        return apiRequest { api.applyOD(userId,strStartDate,strEndDate,subLocationId,strRemarks, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - - - - - -  API for APPLYING Short Leave - - - - - - - - - - -
    suspend fun applySL(userId: Int,strDate: String, noOfHours: String,strRemarks: String): DefaultMessageResponse {
        return apiRequest { api.applySL(userId,strDate,noOfHours,strRemarks, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - -  API for Applied Short Leave History - - - - - - - - - - -
    suspend fun getSLHistory(userId: Int): SLHistoryResponse {
        return apiRequest { api.getSLHistory(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - - API for Pending SL Requests - - - - - - - - - - -
    suspend fun getShortLeaveForApproval(userId: Int): SLForApprovalResponse {
        return apiRequest { api.getShortLeaveForApproval(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    // - - - - - -  API for Approval/Disapproval of Short Leaves - - - - - - - - - - -
    suspend fun approveRejectSL(userId: Int,requestId: Int,type:Int): DefaultMessageResponse {
        return apiRequest { api.approveRejectSL(userId,requestId,type, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }





    // - - - - - -  API for Sub-Location List - - - - - - - - - - -
    suspend fun getSubLocationList(userId: Int): SubLocationsResponse {
        return apiRequest { api.getSubLocationList(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - - - - - -  API for OD List - - - - - - - - - - -
    suspend fun getODList(userId: Int): ODListResponse {
        return apiRequest { api.getODList(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - - - - - -  API for Pending OD Requests - - - - - - - - - - -
    suspend fun getODListForApproval(userId: Int): ODApprovalResponse {
        return apiRequest { api.getODForApproval(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


    // - - - - - -  API for Approval/Disapproval of OD - - - - - - - - - - -
    suspend fun approveRejectOD(userId: Int,requestId: Int,type:Int): DefaultMessageResponse {
        return apiRequest { api.approveRejectOD(userId,requestId,type, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }


}