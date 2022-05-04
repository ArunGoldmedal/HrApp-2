package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.LimitPartyDetailData
import com.google.gson.annotations.SerializedName

data class LimitPartyDetailResponse(
        @SerializedName("data")
        val data: List<LimitPartyDetailData?>? = null,
        val message: String? = null,
        val result: String? = null,
        val servertime: String? = null
)