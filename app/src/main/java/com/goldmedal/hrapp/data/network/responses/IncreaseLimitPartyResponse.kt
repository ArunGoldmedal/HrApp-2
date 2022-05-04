package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.IncreaseLimitPartyData
import com.google.gson.annotations.SerializedName

data class IncreaseLimitPartyResponse(
        @SerializedName("data")
        val data: List<IncreaseLimitPartyData?>? = null,
        val message: String? = null,
        val result: String? = null,
        val servertime: String? = null
)