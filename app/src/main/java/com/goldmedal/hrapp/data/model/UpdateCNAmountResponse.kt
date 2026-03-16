package com.goldmedal.hrapp.data.model


import com.google.gson.annotations.SerializedName

data class UpdateCNAmountResponse(
    @SerializedName("Data")
    val updateItemList: List<UpdateCNAmountItem>,
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

data class UpdateCNAmountItem(
    @SerializedName("Result")
    val result: String
)