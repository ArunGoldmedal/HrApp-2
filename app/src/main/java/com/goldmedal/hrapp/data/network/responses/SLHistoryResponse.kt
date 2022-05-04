package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.SLHistoryData
import com.google.gson.annotations.SerializedName

data class SLHistoryResponse(
        @SerializedName("Data")
        val data: List<SLHistoryData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)