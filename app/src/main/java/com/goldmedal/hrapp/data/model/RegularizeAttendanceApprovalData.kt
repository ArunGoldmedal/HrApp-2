package com.goldmedal.hrapp.data.model

import com.google.gson.annotations.SerializedName

data class RegularizeAttendanceApprovalData(
    val ApprovalStatus: String?,
    val EmployeeName: String?,
    val ID: Int?,
    val NewPunchIn: String?,
    val NewPunchOut: String?,
    val OldPunchIn: String?,
    val OldPunchOut: String?,
    val PunchDate: String?,
    val Remark: String?,
    val AppliedOn: String?,
    val BranchName: String?,
    val DepartmentName: String?,
    @SerializedName("Resaon")
    val Reason: String?
)