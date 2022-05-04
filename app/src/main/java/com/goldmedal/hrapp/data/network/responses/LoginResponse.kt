package com.goldmedal.hrapp.data.network.responses


import com.goldmedal.hrapp.data.db.entities.User
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class LoginResponse(
        @SerializedName("Data")
        val user: List<User>?,
        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,
        val StatusCode: String?,
        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)