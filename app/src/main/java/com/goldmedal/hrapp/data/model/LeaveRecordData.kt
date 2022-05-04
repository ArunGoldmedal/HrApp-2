package com.goldmedal.hrapp.data.model

data class LeaveRecordData(
        val EmployeeID: String? = null,
        val LeaveType: String? = null,
        val LeaveDate: String? = null,
        val AppliedDate: String? = null,
        val StartDate: String? = null,
        val EndDate: String? = null,
        val LeaveStatus: String? = null,
        val LeaveReason: String? = null,
        val ApplyLeaveID: Int? = null,
        val LeaveTypeColor: String? = null,
        val DayType: String? = null,
        val LeaveDays: Double? = 0.0,
        val PaidLeave: Double? = 0.0,
        val UnPaidLeave: Double? = 0.0,
        var ChipTextColor: Int?,
        var ChipBackgroundColor: Int?
)