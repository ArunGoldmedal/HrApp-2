package com.goldmedal.hrapp.ui.dashboard.notification

import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.NotificationRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
        private val repository: NotificationRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getLoggedInUser()


    var apiListener: ApiStageListener<Any>? = null


    fun fetchNotifications(userID: Int?) {

        apiListener?.onStarted("notification_feeds")

        Coroutines.main {
            try {


                val feedsResponse = userID?.let { repository.fetchNotifications(it) }
                if (feedsResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                if (!feedsResponse?.feeds?.isNullOrEmpty()!!) {
                    feedsResponse.feeds.let {
                        apiListener?.onSuccess(it, "notification_feeds")
                        print("inside all holiday- - - " + it.size)
                        return@main
                    }
                }
                } else {
                    val errorResponse = feedsResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "notification_feeds", false) }


                    }
                }
            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "notification_feeds", true)
            } catch (e: NoInternetException) {


                apiListener?.onError(e.message!!, "notification_feeds", true)
                print("Internet not available")
            }catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "notification_feeds", true)
            }
        }

    }
}