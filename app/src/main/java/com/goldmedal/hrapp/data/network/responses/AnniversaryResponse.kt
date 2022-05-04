package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.AnniversaryData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class AnniversaryResponse(
        @SerializedName("Data")
        val anniversaryData: List<AnniversaryData>?,


        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,

        val Errors: List<ErrorData?>?
)