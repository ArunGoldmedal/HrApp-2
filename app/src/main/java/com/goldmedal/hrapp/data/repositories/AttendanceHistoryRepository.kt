package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.AttendanceHistoryResponse
import javax.inject.Inject

class AttendanceHistoryRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {

    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getUser() = db.getUserDao().getUser()




    suspend fun getAttendanceHistory(userId: Int,attendanceStatus: Int): AttendanceHistoryResponse {
        return apiRequest {
            api.attendanceHistory(userId,attendanceStatus, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }
}