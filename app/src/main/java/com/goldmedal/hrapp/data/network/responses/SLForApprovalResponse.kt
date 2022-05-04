package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.SLForApprovalData
import com.google.gson.annotations.SerializedName

data class SLForApprovalResponse(
        @SerializedName("Data")
        val data: List<SLForApprovalData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)