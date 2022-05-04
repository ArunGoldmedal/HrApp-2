package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AttendanceRegularizeByUserData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AttendanceRegularizeByUserResponse(
        @SerializedName("Data")
        val data: List<AttendanceRegularizeByUserData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)