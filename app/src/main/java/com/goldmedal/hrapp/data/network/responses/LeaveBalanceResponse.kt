package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName


data class LeaveBalanceResponse(
        @SerializedName("Data")
        val leaveBalData: List<LeaveBalanceData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)