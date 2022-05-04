package com.goldmedal.hrapp.data.model

data class AttendanceSummaryData(
    val AbsentDays: Double?,
    val Approvedleave: Double?,
    val CompanyHoliday: Double?,
    val Openleave: Double?,
    val PresentDays: Double?,
    val TotalDays: Double?,
    val WeekendDays: Double?,
    val punchoutmissing: Double?
)