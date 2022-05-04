package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.InsertPunchData
import com.google.gson.annotations.SerializedName

data class InsertPunchResponse(@SerializedName("Data")

                               val punchData: List<InsertPunchData>?,

                               val StatusCode: String?,

                               @SerializedName("StatusCodeMessage")
                               val StatusCodeMessage: String?,

                               @SerializedName("Timestamp")
                               val servertime: String?,

                               val Errors: List<ErrorData?>?)






