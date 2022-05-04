package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.LeaveApplyData
import com.google.gson.annotations.SerializedName

data class LeaveApplyResponse(
        @SerializedName("Data")
        val leaveApplyData: List<LeaveApplyData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)