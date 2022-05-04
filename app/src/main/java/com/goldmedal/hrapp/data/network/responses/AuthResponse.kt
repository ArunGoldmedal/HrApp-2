package com.goldmedal.hrapp.data.network.responses


import com.goldmedal.hrapp.data.model.AuthData
import com.google.gson.annotations.SerializedName

data class AuthResponse(
        @SerializedName("data")
    val data: List<AuthData?>?,
        @SerializedName("message")
    val message: String?,
        @SerializedName("result")
    val result: Boolean?,
        @SerializedName("servertime")
    val servertime: String?
)