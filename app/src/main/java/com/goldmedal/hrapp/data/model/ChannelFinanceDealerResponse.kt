package com.goldmedal.hrapp.data.model


import com.google.gson.annotations.SerializedName

data class ChannelFinanceDealerResponse(
    @SerializedName("Data")
    val dealerList: List<ChannelFinanceDealerItem>,
    @SerializedName("Errors")
    val errors: List<ErrorData>,
    @SerializedName("Size")
    val size: Int,
    @SerializedName("StatusCode")
    val statusCode: Int,
    @SerializedName("StatusCodeMessage")
    val statusCodeMessage: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("Version")
    val version: String
)

data class ChannelFinanceDealerItem(
    @SerializedName("cinnum")
    val cinnum: String,
    @SerializedName("dealnm")
    val dealnm: String
)