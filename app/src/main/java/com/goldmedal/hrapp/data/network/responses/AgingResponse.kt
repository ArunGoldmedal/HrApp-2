package com.goldmedal.hrapp.data.db.entities

import com.goldmedal.hrapp.data.model.AgingData
import com.google.gson.annotations.SerializedName

data class AgingResponse(
        @SerializedName("data")
        val data: List<AgingData?>? = null,
        val message: String? = null,
        val result: Boolean? = null,
        val servertime: String? = null
)