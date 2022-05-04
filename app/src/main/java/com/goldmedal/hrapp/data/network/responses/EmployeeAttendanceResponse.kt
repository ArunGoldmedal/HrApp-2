package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.EmployeeAttendanceData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class EmployeeAttendanceResponse(
        @SerializedName("Data")
        val employeeAttendanceData: List<EmployeeAttendanceData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)