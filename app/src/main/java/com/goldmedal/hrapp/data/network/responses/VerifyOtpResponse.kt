package com.goldmedal.hrapp.data.network.responses


import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.VerifyOtpData
import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
        @SerializedName("Data")
        val verifyOtpMain: List<VerifyOtpData>?,


        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)