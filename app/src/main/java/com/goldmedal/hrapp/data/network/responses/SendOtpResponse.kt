package com.goldmedal.hrapp.data.db.entities

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.SendOtpData
import com.google.gson.annotations.SerializedName

data class SendOtpResponse(
        @SerializedName("Data")
        val getOtpMain: List<SendOtpData>?,

        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)