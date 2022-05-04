package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.ProfilePicData
import com.google.gson.annotations.SerializedName

data class ProfilePicResponse(
        @SerializedName("Data")
        val getProfileLink: List<ProfilePicData>?,

        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)