package com.goldmedal.hrapp.data.model

import com.google.gson.annotations.SerializedName

data class AttendanceRegularizeByUserData(
    val ApprovalStatus: String?,
    val EmployeeName: String?,
    val ID: Int?,
    val BranchName: String?,
    val DepartmentName: String?,
    val AppliedOn: String?,
    val NewPunchIn: String?,
    val NewPunchOut: String?,
    val OldPunchIn: String?,
    val OldPunchOut: String?,
    val PunchDate: String?,
    val Remark: String?,
    @SerializedName("Resaon")
    val Reason: String?
)