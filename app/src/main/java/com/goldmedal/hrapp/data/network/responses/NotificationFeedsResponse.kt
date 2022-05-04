package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.NotificationFeeds
import com.google.gson.annotations.SerializedName

data class NotificationFeedsResponse(
        @SerializedName("Data")
        val feeds: List<NotificationFeeds>?,

        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val servertime: String?,

        val Errors: List<ErrorData?>?
)