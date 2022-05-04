package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.DefaultMessageData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class DefaultMessageResponse(
        @SerializedName("Data")
        val data: List<DefaultMessageData>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)