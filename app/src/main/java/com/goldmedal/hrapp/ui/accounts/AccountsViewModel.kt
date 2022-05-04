package com.goldmedal.hrapp.ui.accounts

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.data.repositories.AccountsRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
        private val repository: AccountsRepository
) : ViewModel() {

    var strPartyId: String? = null
    var strSearchText: String? = null

    var detailListener: DetailListener? = null

       fun getAccountDetailData() {
        detailListener?.onStarted()

        Coroutines.main {
            try {
                val getIncreaseLimitResponse = repository.getIncreaseLimitParty("1", "")

                getIncreaseLimitResponse[0].data?.let {
                    detailListener?.onSuccess(it, null, null)
                    return@main
                }
                detailListener?.onFailure("Error","server")

                //   detailListener?.onFailure(getIncreaseLimitResponse[0].message!!)
            } catch (e: ApiException) {
                detailListener?.onFailure(e.message!!,"server")
            } catch (e: NoInternetException) {
                detailListener?.onFailure(e.message!!,"net")
            }
        }

    }


    fun getAgingData() {
        detailListener?.onStarted()

        Coroutines.main {
            try {
                val getAgingResponse = repository.getAgingDetail("999999", "clientsecret")

                getAgingResponse[0].data!![0]?.agingDetails.let {
                    detailListener?.onSuccess(null, it, null)
                    return@main
                }
                detailListener?.onFailure("Error","server")

            } catch (e: ApiException) {
                detailListener?.onFailure(e.message!!,"server")
            } catch (e: NoInternetException) {
                detailListener?.onFailure(e.message!!,"net")
            }
        }

    }


    fun getPartyDetailData() {
        detailListener?.onStarted()

        Coroutines.main {
            try {
                val getPartyDetailResponse = repository.getIncreaseLimitDetail("1", strSearchText ?: "")

                getPartyDetailResponse[0].data?.let {
                    detailListener?.onSuccess(null, null, it)
                    return@main
                }
                detailListener?.onFailure("Error","server")

            } catch (e: ApiException) {
                detailListener?.onFailure(e.message!!,"server")
            } catch (e: NoInternetException) {
                detailListener?.onFailure(e.message!!,"net")
            }
        }

    }


    fun onLimitDetails(view: View) {
        Intent(view.context, PartyDetailActivity::class.java).also {
            view.context.startActivity(it)
        }
    }



}