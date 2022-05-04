package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AttendanceSummaryData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AttendanceSummaryResponse(
        @SerializedName("Data")
        val data: List<AttendanceSummaryData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)