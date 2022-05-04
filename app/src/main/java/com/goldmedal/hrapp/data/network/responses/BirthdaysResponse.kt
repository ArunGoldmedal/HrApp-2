package com.goldmedal.hrapp.data.network.responses

import androidx.room.PrimaryKey
import com.goldmedal.hrapp.data.db.entities.BirthdayData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class BirthdaysResponse(
        @PrimaryKey(autoGenerate = false)

        @SerializedName("Data")
        val birthdayData: List<BirthdayData?>?,


        val StatusCode: String?,

        @SerializedName("StatusCodeMessage")
        val StatusCodeMessage: String?,

        @SerializedName("Timestamp")
        val Timestamp: String?,


        val Errors: List<ErrorData?>?
)