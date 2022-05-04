package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.LeaveReasonsData
import com.google.gson.annotations.SerializedName

data class LeaveReasonsResponse(
        @SerializedName("Data")
        val leaveReasonsData: List<LeaveReasonsData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)