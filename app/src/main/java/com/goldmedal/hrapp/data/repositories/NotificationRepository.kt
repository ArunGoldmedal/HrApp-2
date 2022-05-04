package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.NotificationFeedsResponse
import javax.inject.Inject

class NotificationRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {

    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()




    suspend fun fetchNotifications(userId: Int): NotificationFeedsResponse {
        return apiRequest {
            api.fetchNotifications(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }
   }