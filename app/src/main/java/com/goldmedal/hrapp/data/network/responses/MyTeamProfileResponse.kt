package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.MyTeamProfileData
import com.google.gson.annotations.SerializedName

data class MyTeamProfileResponse(
        @SerializedName("Data")
        val data: List<MyTeamProfileData>?,
        val StatusCode: String?,
        val StatusCodeMessage: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)