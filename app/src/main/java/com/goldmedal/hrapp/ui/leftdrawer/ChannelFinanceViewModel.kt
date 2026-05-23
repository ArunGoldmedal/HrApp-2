package com.goldmedal.hrapp.ui.leftdrawer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.db.entities.HolidayData
import com.goldmedal.hrapp.data.model.ChannelFinanceDealerItem
import com.goldmedal.hrapp.data.model.ChannelFinanceDealerResponse
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.Resource
import com.goldmedal.hrapp.data.repositories.ChannelFinanceRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ChannelFinanceViewModel @Inject constructor(
    private val repository: ChannelFinanceRepository
): ViewModel() {
    var apiListener: ApiStageListener<Any>? = null

    fun getLoggedInUser() = repository.getUser()

    fun getChannelFinanceDealers() {
        apiListener?.onStarted("channel_finance_dealers")

        Coroutines.main {
            try {
                val channelFinanceDealersResponse = repository.getChannelFinanceDealers()

                if (channelFinanceDealersResponse.statusCode == 200) {
                    if (channelFinanceDealersResponse.dealerList.isNotEmpty()) {
                        channelFinanceDealersResponse.dealerList.let {
                            apiListener?.onSuccess(it, "channel_finance_dealers")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = channelFinanceDealersResponse.errors
                    if (errorResponse.isNotEmpty()) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(it, "channel_finance_dealers", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message.toString(), "channel_finance_dealers", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message.toString(), "channel_finance_dealers", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message.toString(), "channel_finance_dealers", true)
            }
        }

    }

    fun getChannelFinanceDealerWiseData(cinNumber: String) {
        apiListener?.onStarted("channel_finance_dealer_wise_data")

        Coroutines.main {
            try {
                val response = repository.getChannelFinanceDealerWise(cinNumber)

                if (response.statusCode == 200) {
                    if (response.dealerWiseDataList.isNotEmpty()) {
                        response.dealerWiseDataList.let {
                            apiListener?.onSuccess(it, "channel_finance_dealer_wise_data")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.errors
                    if (errorResponse.isNotEmpty()) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(it, "channel_finance_dealer_wise_data", false)
                        }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message.toString(), "channel_finance_dealer_wise_data", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message.toString(), "channel_finance_dealer_wise_data", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message.toString(), "channel_finance_dealer_wise_data", true)
            }
        }
    }

    fun updateCFAmount(cinNumber: String, userId: Int, amount: String, slNo: Int) {
        apiListener?.onStarted("update_channel_finance_amount")

        Coroutines.main {
            try {
                val response = repository.updateCFAmount(cinNumber, userId, amount, slNo)

                if (response.statusCode == 200) {
                    if (response.updateItemList.isNotEmpty()) {
                        response.updateItemList.let {
                            apiListener?.onSuccess(it, "update_channel_finance_amount")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = response.errors
                    if (errorResponse.isNotEmpty()) {
                        errorResponse[0].ErrorMsg?.let {
                            apiListener?.onError(it, "update_channel_finance_amount", false)
                        }
                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message.toString(), "update_channel_finance_amount", true)
            } catch (e: NoInternetException) {
                print("Internet not available")
                apiListener?.onError(e.message.toString(), "update_channel_finance_amount", true)
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message.toString(), "update_channel_finance_amount", true)
            }
        }
    }
}