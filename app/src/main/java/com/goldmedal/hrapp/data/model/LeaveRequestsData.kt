package com.goldmedal.hrapp.data.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
data class LeaveRequestsData(
        val ApplyLeaveID: Int? = 0,
        val LeaveType: String? = null,
        val LeaveImage: String? = null,
        val FromDate: String? = null,
        val ToDate: String? = null,
        val LeaveReason: String? = null,
        val AppliedLeavesDay: Double? = null,
        val ActualLeavesDay: Double? = null,
        val AvailableLeaves: Double? = null,
        val Status: String? = null,
        val EmployeeName: String? = null,
        val EmployeeCode: String? = null,
        val EmployeeID: Int? = null,
        val BranchName: String? = null,
        val DepartmentName: String? = null,
        val ProfilePicture: String? = null,
        val GenderID: Int? = 1,
        val EmployeeDesignation: String? = null,
        val LeaveDuration: String? = null,
        val LeaveAppliedOn: String? = null,
        val ReportingPersonName: String? = null,
        val ReportingPersonProfilePic: String? = null,
        val ReportingPersonGenderId: String? = null,
        val ReportingPersonDesignation: String? = null,
        val LeaveTypeColor: String? = null,
        val DayType: String? = null,
        var ChipTextColor: Int? = null,
        var ChipBackgroundColor: Int? = null

) : Serializable, Parcelable {
}