package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.model.ChannelFinanceDealerResponse
import com.goldmedal.hrapp.data.model.ChannelFinanceDealerWiseResponse
import com.goldmedal.hrapp.data.model.UpdateCNAmountResponse
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import javax.inject.Inject

class ChannelFinanceRepository @Inject constructor(
    private val api: MyApi,
    private val db: AppDatabase
): SafeApiRequest() {

    fun getUser() = db.getUserDao().getUser()

    suspend fun getChannelFinanceDealers(): ChannelFinanceDealerResponse {
        return apiRequest { api.getCFDealer() }
    }

    suspend fun getChannelFinanceDealerWise(cinNumber: String): ChannelFinanceDealerWiseResponse {
        return apiRequest { api.getCFDealerWiseData(cinNumber) }
    }

    suspend fun updateCFAmount(
        cinNumber: String,
        userId: Int,
        amount: String,
        slNo: Int
    ): UpdateCNAmountResponse {
        return apiRequest { api.updateCFAmount(cinNumber, userId, amount, slNo) }
    }
}