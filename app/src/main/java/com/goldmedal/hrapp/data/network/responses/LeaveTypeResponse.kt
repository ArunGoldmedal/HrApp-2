package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.google.gson.annotations.SerializedName

data class LeaveTypeResponse(
        @SerializedName("Data")
        val leaveTypeData: List<LeaveTypeData>?,


        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)