package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.google.gson.annotations.SerializedName

data class LeaveRequestsResponse(
        @SerializedName("Data")
        val leaveRequestsData: List<LeaveRequestsData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)