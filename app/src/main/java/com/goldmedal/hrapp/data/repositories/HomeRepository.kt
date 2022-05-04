package com.goldmedal.hrapp.data.repositories

import com.goldmedal.hrapp.data.db.AppDatabase
import com.goldmedal.hrapp.data.db.entities.*
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.MyApi
import com.goldmedal.hrapp.data.network.SafeApiRequest
import com.goldmedal.hrapp.data.network.responses.*
import javax.inject.Inject

class HomeRepository @Inject constructor(
        private val api: MyApi,
        private val db: AppDatabase
) : SafeApiRequest() {

    /*  - - - - - - - - - - - - -   Active User - - - - - - - - - - - -  */
    fun getLoggedInUser() = db.getUserDao().getUser()


    /*  - - - - - - - - - - - - -   BIRTHDAY - - - - - - - - - - - -  */

    suspend fun upcomingBirthdays(userId: Int): BirthdaysResponse {
        return apiRequest {
            api.upcomingBirthdays(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }

    suspend fun saveBirthData(birthData: List<BirthdayData?>) = db.getBirthDateDao().insertBirthDate(birthData)

    fun getBirthData() = db.getBirthDateDao().getBirthDate()

    suspend fun removeBirthData() = db.getBirthDateDao().removeBirthDate()


    /*  - - - - - - - - - - - - -   HOLIDAY - - - - - - - - - - - -  */

    suspend fun upcomingHolidays(userId: Int, holidayType: String): HolidaysResponse {
        return apiRequest {
            api.upcomingHolidays(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET, holidayType)
        }
    }

    suspend fun getAllHolidays(userId: Int, holidayType: String): AllHolidaysResponse {
        return apiRequest {
            api.getAllHolidays(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET, holidayType)
        }
    }

    suspend fun saveHolidaysData(holidayData: List<HolidayData?>) = db.getHolidayDao().insertHoliday(holidayData)

    fun getHolidaysData() = db.getHolidayDao().getHoliday()

    suspend fun removeHolidaysData() = db.getHolidayDao().removeHoliday()


    suspend fun saveAllHolidaysData(holidayData: List<AllHolidayData?>) = db.getAllHolidayDao().insertAllHoliday(holidayData)

    fun getAllHolidaysData() = db.getAllHolidayDao().getAllHoliday()

    suspend fun removeAllHolidaysData() = db.getAllHolidayDao().removeAllHoliday()


    /*  - - - - - - - - - - - - -   ANNIVERSARY - - - - - - - - - - - -  */

    suspend fun upcomingAnniversary(userId: Int): AnniversaryResponse {
        return apiRequest { api.upcomingAnniversary(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    suspend fun saveAnniversaryData(anniversaryData: List<AnniversaryData?>) = db.getAnniversaryDateDao().insertAnniversaryDate(anniversaryData)

    fun getAnniversaryData() = db.getAnniversaryDateDao().getAnniversaryDate()

    suspend fun removeAnniversaryData() = db.getAnniversaryDateDao().removeAnniversaryData()


    /*  - - - - - - - - - - - - -   EMPLOYEE ATTENDANCE - - - - - - - - - - - -  */

    suspend fun employeeAttendance(userId: Int): EmployeeAttendanceResponse {
        return apiRequest { api.employeeAttendance(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
    }

    suspend fun saveEmployeeAttendance(empData: List<EmployeeAttendanceData?>) = db.getEmployeeAttendanceDao().insertEmpAttendanceData(empData)

    fun getEmpAttendanceData() = db.getEmployeeAttendanceDao().getEmpAttendanceData()

    suspend fun removeEmpAttendanceData() = db.getEmployeeAttendanceDao().removeEmpAttendanceData()


    /*  - - - - - - - - - - - - -   PUNCH STATUS - - - - - - - - - - - -  */
    suspend fun punchInoutStatus(strUserId: Int): PunchInOutStatusResponse {
        return apiRequest {
            api.getPunchInOutStatus(strUserId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }

    /*  - - - - - - - - - - - - -   MY TEAM - - - - - - - - - - - -  */
    suspend fun getMyTeam(userId: Int): MyTeamResponse {
        return apiRequest {
            api.getMyTeam(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }

    /*  - - - - - - - - - - - - -   MY TEAM PROFILE- - - - - - - - - - - -  */
    suspend fun getMyTeamProfile(userId: Int,year:String): MyTeamProfileResponse {
        return apiRequest {
            api.getMyTeamProfile(userId,year, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }

    /*  - - - - - - - - - - - - -   PENDING REQUESTS COUNT - - - - - - - - - - - -  */
    suspend fun getPendingRequestsCount(userId: Int): RequestsCountResponse {
        return apiRequest {
            api.getPendingRequestsCount(userId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET)
        }
    }


    suspend fun saveMyTeam(data: List<MyTeamData?>) = db.getMyTeamDao().upsert(data = data)

    fun getMyTeam() = db.getMyTeamDao().getMyTeam()

    suspend fun removeMyTeamData() = db.getMyTeamDao().removeTeamData()

}