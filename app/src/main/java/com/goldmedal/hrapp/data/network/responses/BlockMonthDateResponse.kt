package com.goldmedal.hrapp.data.network.responses


import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class BlockMonthDateResponse(
    @SerializedName("Data")
    val blockMonthDateData: List<BlockMonthDateData>,
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

data class BlockMonthDateData(
    @SerializedName("BlockDay")
    val blockDay: Int,
    @SerializedName("BlockMonth")
    val blockMonth: Int,
    @SerializedName("BlockYear")
    val blockYear: Int,
    @SerializedName("Monthblockdate")
    val monthblockdate: String
)