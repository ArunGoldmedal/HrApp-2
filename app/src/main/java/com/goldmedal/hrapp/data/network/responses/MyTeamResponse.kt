package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class MyTeamResponse(
        @SerializedName("Data")
        val data: List<MyTeamData>?,
        val StatusCode: String?,
        val StatusCodeMessage: String?,
        val Timestamp: String?,
        val Errors: List<ErrorData?>?
)