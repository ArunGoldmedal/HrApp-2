package com.goldmedal.hrapp.ui.dashboard.home


import android.util.Log
import androidx.lifecycle.ViewModel
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.repositories.HomeRepository
import com.goldmedal.hrapp.util.ApiException
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.NoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.SocketTimeoutException
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val repository: HomeRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getLoggedInUser()
    fun getBirthDataDetail() = repository.getBirthData()
    fun getAnniversaryDataDetail() = repository.getAnniversaryData()
    fun getHolidayDataDetail() = repository.getHolidaysData()
    fun getAllHolidayDataDetail() = repository.getAllHolidaysData()
    fun getEmployeeAttendanceData() = repository.getEmpAttendanceData()
    fun getMyTeamData() = repository.getMyTeam()

    var apiListener: ApiStageListener<Any>? = null


    fun allHolidays(userID: Int?) {


        if (userID == null) {
            apiListener?.onValidationError("User id cannot be nil", "all_holidays")
            return
        }

        apiListener?.onStarted("all_holidays")


        Coroutines.main {
            try {
                val holidaysResponse = repository.getAllHolidays(userID, "ALL")
                if (holidaysResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {

                    if (!holidaysResponse?.holidayData?.isNullOrEmpty()!!) {
                        holidaysResponse.holidayData.let {
                            apiListener?.onSuccess(it, "all_holidays")
                            Log.d("Inside all holiday", "Msg - - - -" + it.size)
                            println("inside all holiday - - - - - -  - --  - - - - - -")
                            repository.removeAllHolidaysData()
                            repository.saveAllHolidaysData(it)
                            print("inside all holiday- - - " + it.size)
                            return@main
                        }
                    }
                } else {

                    if (holidaysResponse.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeAllHolidaysData()
                    }

                    val errorResponse = holidaysResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {


                            apiListener?.onError(it, "all_holidays", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "all_holidays", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "all_holidays", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "all_holidays", true)
            }
        }

    }


    fun upcomingHolidays(userId: Int?) {
        apiListener?.onStarted("holidays")
// ( ALL ) = ALL HOLIDAY LIST
// ( - ) = UPCOMING HOLIDAY LIST

        Coroutines.main {
            try {
                val holidaysResponse = userId?.let { repository.upcomingHolidays(it, "-") }

                if (holidaysResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!holidaysResponse?.holidayData?.isNullOrEmpty()!!) {
                        holidaysResponse.holidayData.let {
                            apiListener?.onSuccess(it, "holidays")
                            Log.d("Inside holiday", "Msg - - - -" + it.size)
                            println("inside holiday - - - - - -  - --  - - - - - -")
                            repository.removeHolidaysData()
                            repository.saveHolidaysData(it)
                            print("inside holiday- - - " + it.size)
                            return@main
                        }
                    }
                } else {
                    if (holidaysResponse?.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeHolidaysData()
                    }
                    val errorResponse = holidaysResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "holidays", false)
                        }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "holidays", true)
            } catch (e: NoInternetException) {

                print("Internet not available")
                apiListener?.onError(e.message!!, "holidays", true)

            } catch (e: SocketTimeoutException) {
                apiListener?.onError(e.message!!, "holidays", true)


            }
        }

    }


    fun upcomingBirthdays(userId: Int?) {

        apiListener?.onStarted("birthday")

        Coroutines.main {
            try {
                val birthdayResponse = userId?.let { repository.upcomingBirthdays(it) }
                if (birthdayResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!birthdayResponse?.birthdayData?.isNullOrEmpty()!!) {
                        birthdayResponse.birthdayData.let {
                            apiListener?.onSuccess(it, "birthday")
                            Log.d("Inside", "Msg - - - -" + it.size)
                            println("inside - - - - - -  - --  - - - - - -")
                            repository.removeBirthData()
                            repository.saveBirthData(it)
                            print("inside - - - " + it.size)
                            return@main
                        }
                    }
                } else {

                    if (birthdayResponse?.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeBirthData()
                    }

                    val errorResponse = birthdayResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {


                            apiListener?.onError(it, "birthday", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "birthday", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "birthday", true)

            } catch (e: SocketTimeoutException) {
                Log.d("NEW EXCEPTION", e.toString())
                apiListener?.onError(e.message!!, "birthday", true)
            }
        }
    }


    fun upcomingAnniversary(userId: Int?) {

        apiListener?.onStarted("anniversary")

        Coroutines.main {
            try {
                val anniversaryResponse = userId?.let { repository.upcomingAnniversary(it) }
                if (anniversaryResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!anniversaryResponse?.anniversaryData?.isNullOrEmpty()!!) {
                        anniversaryResponse.anniversaryData.let {
                            apiListener?.onSuccess(it, "anniversary")
                            Log.d("Inside anniversary", "Msg - - - -" + it.size)
                            println("inside anniversary - - - - - -  - --  - - - - - -")
                            repository.removeAnniversaryData()
                            repository.saveAnniversaryData(it)
                            print("inside - - - " + it.size)
                            return@main
                        }
                    }
                } else {

                    if (anniversaryResponse?.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeAnniversaryData()
                    }

                    val errorResponse = anniversaryResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "anniversary", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "anniversary", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "anniversary", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                Log.d("NEW EXCEPTION", e.toString())
                apiListener?.onError(e.message!!, "anniversary", true)
            }
        }

    }


    fun employeeAttendance(userId: Int?) {

        apiListener?.onStarted("employee_attendance")

        Coroutines.main {
            try {
                val employeeAttendanceResponse = userId?.let { repository.employeeAttendance(it) }
                if (employeeAttendanceResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!employeeAttendanceResponse?.employeeAttendanceData?.isNullOrEmpty()!!) {
                        employeeAttendanceResponse.employeeAttendanceData.let {
                            apiListener?.onSuccess(it, "employee_attendance")

                            repository.removeEmpAttendanceData()
                            repository.saveEmployeeAttendance(it)

                            return@main
                        }
                    }
                } else {
                    if (employeeAttendanceResponse?.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeEmpAttendanceData()
                    }
                    val errorResponse = employeeAttendanceResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            apiListener?.onError(it, "employee_attendance", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "employee_attendance", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "employee_attendance", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                Log.d("NEW EXCEPTION", e.toString())
                apiListener?.onError(e.message!!, "employee_attendance", true)
            }
        }

    }


    fun punchInoutStatus(userId: Int?) {

        apiListener?.onStarted("punchStatus")

        Coroutines.main {
            try {
                val punchStatusResponse = userId?.let { repository.punchInoutStatus(it) }

                if (punchStatusResponse?.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!punchStatusResponse?.punchStatusData?.isNullOrEmpty()!!) {
                        punchStatusResponse.punchStatusData.let {
                            apiListener?.onSuccess(it, "punchStatus")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = punchStatusResponse?.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "punchStatus", false) }


                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "punchStatus", true)
            } catch (e: NoInternetException) {

                apiListener?.onError(e.message!!, "punchStatus", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {
                Log.d("NEW EXCEPTION", e.toString())
                apiListener?.onError(e.message!!, "punchStatus", true)
            }
        }

    }


    fun getMyTeam(userId: Int?) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "my_team")
            return
        }

        apiListener?.onStarted("my_team")

        Coroutines.main {
            try {
                val outputResponse =  repository.getMyTeam(userId)
                if (outputResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!outputResponse.data?.isNullOrEmpty()!!) {
                        outputResponse.data.let {
                            apiListener?.onSuccess(it, "my_team")

                            repository.removeMyTeamData()
                            repository.saveMyTeam(it)


                            return@main
                        }
                    }
                } else {
                    if (outputResponse.StatusCode.equals(GlobalConstant.NO_DATA_CODE)) {
                        repository.removeMyTeamData()
                    }
                    val errorResponse = outputResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "my_team", false) }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "my_team", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "my_team", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {

                apiListener?.onError(e.message!!, "my_team", true)
            }
        }

    }


    fun getMyTeamProfile(userId: Int?,year: String) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "team_profile")
            return
        }

        apiListener?.onStarted("team_profile")

        Coroutines.main {
            try {
                val outputResponse =  repository.getMyTeamProfile(userId,year)
                if (outputResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!outputResponse.data?.isNullOrEmpty()!!) {
                        outputResponse.data.let {
                            apiListener?.onSuccess(it, "team_profile")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "team_profile", false) }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "team_profile", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "team_profile", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {

                apiListener?.onError(e.message!!, "team_profile", true)
            }
        }

    }



    fun getPendingRequestsCount(userId: Int?) {
        if (userId == null) {
            apiListener?.onValidationError("User id cannot be nil", "pending_requests_cnt")
            return
        }

        apiListener?.onStarted("pending_requests_cnt")

        Coroutines.main {
            try {
                val outputResponse =  repository.getPendingRequestsCount(userId)
                if (outputResponse.StatusCode.equals(GlobalConstant.SUCCESS_CODE)) {
                    if (!outputResponse.data?.isNullOrEmpty()!!) {
                        outputResponse.data.let {
                            apiListener?.onSuccess(it, "pending_requests_cnt")
                            return@main
                        }
                    }
                } else {
                    val errorResponse = outputResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let { apiListener?.onError(it, "pending_requests_cnt", false) }
                    }
                }


            } catch (e: ApiException) {
                apiListener?.onError(e.message!!, "pending_requests_cnt", true)
            } catch (e: NoInternetException) {
                apiListener?.onError(e.message!!, "pending_requests_cnt", true)
                print("Internet not available")
            } catch (e: SocketTimeoutException) {

                apiListener?.onError(e.message!!, "pending_requests_cnt", true)
            }
        }

    }
}
