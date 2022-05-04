package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AppliedLeaveCountData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AppliedLeaveCountResponse(
        @SerializedName("Data")
        val leaveCountData: List<AppliedLeaveCountData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)