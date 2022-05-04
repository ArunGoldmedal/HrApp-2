package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.RegularizeAttendanceApprovalData
import com.google.gson.annotations.SerializedName

data class RegularizeAttendanceApprovalResponse(
        @SerializedName("Data")
        val data: List<RegularizeAttendanceApprovalData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)