package com.goldmedal.hrapp.data.db.entities

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.ResetPasswordData
import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(
        @SerializedName("Data")
        val resetPasswordMain: List<ResetPasswordData>?,

        val StatusCodeMessage: String?,

        val StatusCode:String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)