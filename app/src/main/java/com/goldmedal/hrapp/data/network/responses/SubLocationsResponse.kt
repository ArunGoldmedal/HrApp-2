package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.SubLocationsData
import com.google.gson.annotations.SerializedName

data class SubLocationsResponse(
        @SerializedName("Data")
        val data: List<SubLocationsData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)