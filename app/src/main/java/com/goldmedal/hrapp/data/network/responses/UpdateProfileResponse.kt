package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.UserDataUpdate
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
        @SerializedName("Data")
        val user: List<UserDataUpdate>?,
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)