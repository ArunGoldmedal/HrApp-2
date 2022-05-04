package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.db.entities.AgingResponse
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.IncreaseLimitPartyResponse
import com.goldmedal.hrapp.data.network.responses.LimitPartyDetailResponse
import javax.inject.Inject

class AccountsRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {



    // - - - - - -  API for getting list of limit parties - - - - - - - - - - -
    suspend fun getIncreaseLimitParty(partyId: String, searchText: String): List<IncreaseLimitPartyResponse> {
        return apiRequest { api.getIncreaseLimitParty(strPartyId = partyId, strSearchText = searchText) }
    }

    // - - - - - -  API for updating limit party - - - - - - - - - - -
    suspend fun updateLimitParty(cin: String, clientsecret: String): List<AgingResponse> {
        return apiRequest { api.updateLimitParty(strCin = cin,strClientSecret =  clientsecret) }
    }

    // - - - - - -  API for getting Aging details - - - - - - - - - - -
    suspend fun getAgingDetail(cin: String, clientsecret: String): List<AgingResponse> {
        return apiRequest { api.getAgingDetail(strCin = cin,strClientSecret =  clientsecret) }
    }

    // - - - - - -  API for getting increase limit detail - - - - - - - - - - -
    suspend fun getIncreaseLimitDetail(partyId: String, searchText: String): List<LimitPartyDetailResponse> {
        return apiRequest { api.getIncreaseLimitDetail(strPartyId = partyId, strSearchText = searchText) }
    }

}
//  suspend fun saveParty(user: List<IncreaseLimitPartyData>) = db.getUserDao().upsert(user)

//  fun getParty() = db.getUserDao().getuser()

