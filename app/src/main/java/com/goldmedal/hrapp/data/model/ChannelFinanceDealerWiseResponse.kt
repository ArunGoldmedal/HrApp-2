package com.goldmedal.hrapp.data.model


import com.google.gson.annotations.SerializedName

data class ChannelFinanceDealerWiseResponse(
    @SerializedName("Data")
    val dealerWiseDataList: List<DealerWiseData>,
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

data class DealerWiseData(
    @SerializedName("balancelimit")
    val balancelimit: Double,
    @SerializedName("cin")
    val cin: String,
    @SerializedName("outstanding")
    val outstanding: Double,
    @SerializedName("partyid")
    val partyid: Int,
    @SerializedName("slno")
    val slno: Int,
    @SerializedName("totallimit")
    val totallimit: Double,
    @SerializedName("typecat")
    val typecat: Int,
    @SerializedName("AccountFrozon")
    val accountFrozen: String,
    @SerializedName("OutstandingAmount")
    val outstandingAmount: Double
)