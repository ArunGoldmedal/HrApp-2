package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.RegularizeAttendanceApprovalData
import com.goldmedal.hrapp.data.model.RequestsCountData
import com.google.gson.annotations.SerializedName


data class RequestsCountResponse(
        @SerializedName("Data")
        val data: List<RequestsCountData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)