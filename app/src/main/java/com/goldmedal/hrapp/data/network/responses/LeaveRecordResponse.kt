package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.google.gson.annotations.SerializedName

data class LeaveRecordResponse(
        @SerializedName("Data")
        val leaveRecordData: List<LeaveRecordData>?,

        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)