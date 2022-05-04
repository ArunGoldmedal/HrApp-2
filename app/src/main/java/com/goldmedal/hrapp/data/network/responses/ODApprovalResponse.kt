package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.ODApprovalData
import com.goldmedal.hrapp.data.model.ODListData
import com.google.gson.annotations.SerializedName

data class ODApprovalResponse(
        @SerializedName("Data")
        val data: List<ODApprovalData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)