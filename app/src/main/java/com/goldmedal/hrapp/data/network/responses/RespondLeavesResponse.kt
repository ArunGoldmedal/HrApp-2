package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.RespondLeavesData
import com.google.gson.annotations.SerializedName

data class RespondLeavesResponse(
        @SerializedName("Data")
        val respondLeavesData: List<RespondLeavesData>?,

        val StatusCode: String?,
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)