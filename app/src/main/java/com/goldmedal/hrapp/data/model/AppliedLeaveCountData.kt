package com.goldmedal.hrapp.data.model

import com.google.gson.annotations.SerializedName

data class AppliedLeaveCountData(
        @SerializedName("ActualLeaveday")
        val ActualLeaveDays: String? = null,
        val AppliedLeaveDays: String? = null
)
