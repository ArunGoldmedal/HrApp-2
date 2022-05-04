package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AttendanceRegularizationListData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AttendanceRegularizationListResponse(
        @SerializedName("Data")
        val data: List<AttendanceRegularizationListData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)