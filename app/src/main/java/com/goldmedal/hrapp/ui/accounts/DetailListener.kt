package com.goldmedal.hrapp.ui.accounts

import com.goldmedal.hrapp.data.db.entities.IncreaseLimitPartyData
import com.goldmedal.hrapp.data.model.AgingDetail
import com.goldmedal.hrapp.data.model.LimitPartyDetailData

interface DetailListener {
    fun onStarted()
    fun onSuccess(partyList: List<IncreaseLimitPartyData?>?, agingList: List<AgingDetail?>?, partyDetailList: List<LimitPartyDetailData?>?)
    fun onFailure(message: String, reason: String)
}