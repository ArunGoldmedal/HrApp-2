package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AttendanceHistoryData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AttendanceHistoryResponse(
        @SerializedName("Data")
        val attendanceHistoryData: List<AttendanceHistoryData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)