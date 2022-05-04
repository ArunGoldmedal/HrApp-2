package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.InitialApiData
import com.google.gson.annotations.SerializedName

data class InitialApiResponse(
        @SerializedName("Data")
        val data: List<InitialApiData>?,
        val StatusCode: String?,
        val StatusCodeMessage: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)