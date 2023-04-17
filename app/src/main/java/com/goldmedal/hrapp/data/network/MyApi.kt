package com.goldmedal.hrapp.data.network

import com.goldmedal.hrapp.data.db.entities.AgingResponse
import com.goldmedal.hrapp.data.db.entities.ResetPasswordResponse
import com.goldmedal.hrapp.data.db.entities.SendOtpResponse
import com.goldmedal.hrapp.data.model.AddCompanyResponse
import com.goldmedal.hrapp.data.model.CommonImageUploadResponse
import com.goldmedal.hrapp.data.model.GetCompanyDetailsResponse
import com.goldmedal.hrapp.data.network.GlobalConstant.BASE_URL
import com.goldmedal.hrapp.data.network.GlobalConstant.HRM_BASE_URL
import com.goldmedal.hrapp.data.network.responses.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit


interface MyApi {


    // - - - - - Initial API - - - - - -
    @FormUrlEncoded
    @POST("leaves/InitialAPI")
    suspend fun initialApi(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<InitialApiResponse>

    // - - - - - Login API - - - - - -
    @FormUrlEncoded
    @POST("Login/authenticate")
    suspend fun userLogin(
            @Field("UserName") strCin: String,
            @Field("Password") strPassword: String,
            @Field("deviceid") strDeviceId: String,
            @Field("DeviceType") strDeviceType: String,
            @Field("AppVersion") strAppVersion: String,
            @Field("OSVersion") strOSVersion: String,
            @Field("pooswooshid") strFCMToken: String,
            @Field("IP") strIP: String,
            @Field("ClientID") strClientid: String,
            @Field("Lat") strLat: String,
            @Field("Long") strLong: String,
            @Field("ModalType") strModelType: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LoginResponse>

    // - - - - - Update Profile Photo - - - - - -

    @FormUrlEncoded
    @POST("Login/profilePhotoUpdate")
    suspend fun uploadProfilePic(
            @Field("UserID") userId: Int,
            @Field("ProfilePhotoLink") strProfilePhotoLink: String
    ): Response<ProfilePicResponse>



    @FormUrlEncoded
    @POST("Login/editProfile")
    suspend fun updateUserProfile(
            @Field("UserID") userId: Int,
            @Field("FatherName") fatherName: String,
            @Field("MotherName") motherName: String,
            @Field("SpouseName") spouseName: String,
            @Field("AnniversaryDOB") anniversaryDate: String,
            @Field("FatherDOB") fatherDOB: String,
            @Field("MotherDOB") motherDOB: String,
            @Field("SpouseDOB") spouseDOB: String,
            @Field("PersonalEmail") personalEmail: String,
            @Field("Officialemail") officeEmail: String,
            @Field("MobileNo") mobileNo: String,
//            @Field("HomeAddress") homeAddress: String,
            @Field("MartialStatus") martialStatus: Int,
            @Field("ChildName1") childName1: String,
            @Field("ChildName2") childName2: String,
            @Field("ChildName3") childName3: String,
            @Field("ChildDOB1") childDOB1: String,
            @Field("ChildDOB2") childDOB2: String,
            @Field("ChildDOB3") childDOB3: String
    ): Response<UpdateProfileResponse>

    // - - - - - Get otp API - - - - - -
    @FormUrlEncoded
    @POST("otp/sendOtp")
    suspend fun sendOtp(
            @Field("MobileNo") strCin: String,
            @Field("deviceid") strDeviceId: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<SendOtpResponse>

    // - - - - - verify otp API - - - - - -
    @FormUrlEncoded
    @POST("otp/verifyOtp")
    suspend fun verifyOtp(
            @Field("MobileNo") strMobileNo: String,
            @Field("Otp") strOtp: String,
            @Field("RequestNo") strRequestNo: String,
            @Field("deviceid") strDeviceId: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<VerifyOtpResponse>

    // - - - - - Reset password API - - - - - -
    @FormUrlEncoded
    @POST("password/forgetPassword")
    suspend fun resetPassword(
            @Field("UserID") strMobileNo: String,
            @Field("NewPassword") strNewPassword: String,
            @Field("OldPassword") strOldPassword: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<ResetPasswordResponse>

    // - - - - - - - - Upcoming Holidays - - - - - - - -
    //---- Type "All" or "-"
    @FormUrlEncoded
    @POST("events/getHolidayList")
    suspend fun upcomingHolidays(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String,
            @Field("HolidayType") strHolidayType: String
    ): Response<HolidaysResponse>

    @FormUrlEncoded
    @POST("events/getHolidayList")
    suspend fun getAllHolidays(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String,
            @Field("HolidayType") strHolidayType: String
    ): Response<AllHolidaysResponse>

    // - - - - - - - - Upcoming Birthdays - - - - - - - -
    @FormUrlEncoded
    @POST("events/getBirthdaysInWeek")
    suspend fun upcomingBirthdays(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<BirthdaysResponse>


    // - - - - - - - - Upcoming Anniversary - - - - - - - -
    @FormUrlEncoded
    @POST("events/getAnniversaryInWeek")
    suspend fun upcomingAnniversary(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AnniversaryResponse>


    // - - - - - - - - Punch Status  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/punchInOutStatus")
    suspend fun getPunchInOutStatus(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<PunchInOutStatusResponse>


    // - - - - - - - - Insert punch data  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/InsertPunchInandOut")
    suspend fun insertPunchAttendance(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String,
            @Field("Image") strImage: String,
            @Field("Remark") strRemark: String,
            @Field("IP") strIPAddr: String,
            @Field("Latitude") strLat: String,
            @Field("Longitude") strLong: String,
            @Field("DeviceId") strDeviceId: String,
            @Field("PunchType") strPunchType: String,
            @Field("DeviceType") strDeviceType: String,
            @Field("Location") strLocation: String
    ): Response<InsertPunchResponse>


    // - - - - - - - - Get all attendance data  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/AllPunchDataOnDate")
    suspend fun getAllAttendanceData(
            @Field("UserID") userId: Int,
            @Field("FromDate") strFromDate: String,
            @Field("ToDate") strToDate: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<GetAllAttendanceResponse>



//    @FormUrlEncoded
//    @POST("punchdata/getPunchData")
//    suspend fun getCurrentAttendanceData(
//            @Field("UserID") userId: Int,
//            @Field("ClientID") strClientid: String,
//            @Field("ClientSecret") strClientSecret: String
//    ): Response<GetCurrentAttendanceResponse>

    // - - - - - - - - Get current date attendance data  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/AllPunchDataOnDate")
    suspend fun getCurrentAttendanceData(
            @Field("UserID") userId: Int,
            @Field("FromDate") strFromDate: String,
            @Field("ToDate") strToDate: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<GetCurrentAttendanceResponse>

    // - - - - - - - - Get team attendance details data  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/AllPunchDataOnDate")
    suspend fun getAttendanceDetails(
            @Field("UserID") userId: Int,
            @Field("FromDate") strFromDate: String,
            @Field("ToDate") strToDate: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AttendanceDetailsResponse>


    // - - - - - - - - Get team attendance details data  - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getPunchCountDetails")
    suspend fun getAttendanceSummary(
            @Field("UserID") userId: Int,
            @Field("FromDate") strFromDate: String,
            @Field("ToDate") strToDate: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AttendanceSummaryResponse>




    // - - - - - - - - LEAVES - - - - - - - -
    @FormUrlEncoded
    @POST("leaves/getLeavesHistory")
    suspend fun getLeaveRecordDetail(
            @Field("UserID") strUserId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String,
            @Field("LeaveYear") strLeaveYear: String
    ): Response<LeaveRecordResponse>


    @FormUrlEncoded
    @POST("leaves/getLeavesHistory")
    fun getLeaveRecordDetailWorker(
            @Field("UserID") strUserId: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveRecordResponse>

    @FormUrlEncoded
    @POST("leaves/getLeaveTypes")
    suspend fun getLeaveTypeDetail(
            @Field("UserID") strUserId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveTypeResponse>


    @FormUrlEncoded
    @POST("leaves/getActualAppliedCount")
    suspend fun getAppliedLeavesCount(
            @Field("UserID") userId: Int,
            @Field("FromDate") strFromDate: String,
            @Field("Todate") strToDate: String,
            @Field("LeaveConsiderID") leaveConsiderId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AppliedLeaveCountResponse>

    @FormUrlEncoded
    @POST("leaves/applyLeave")
    suspend fun applyLeave(
            @Field("UserID") strUserId: Int,
            @Field("LeaveTypeID") strLeaveTypeID: String,
            @Field("FromDate") strStartDate: String,
            @Field("ToDate") strEndDate: String,
            @Field("LeaveImageData") strLeaveImage: String,
            @Field("LeaveReasonID") strLeaveReasonID: String,
            @Field("AppliedLeavesDay") strAppliedLeavesDay: String,
            @Field("ActualLeavesDay") strActualLeavesDay: String,
            @Field("LeaveConsiderID") strLeaveConsiderId: Int,
            @Field("DayType") strDayType: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveApplyResponse>


    @FormUrlEncoded
    @POST("leaves/getLeaveForApproval")
    suspend fun leaveRequests(
            @Field("UserID") userId: Int,
            @Field("Type") type: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveRequestsResponse>

    @FormUrlEncoded
    @POST("leaves/InsertLeaveApproval")
    suspend fun approveLeaves(
            @Field("UserID") userId: Int,
            @Field("ApplyLeaveID") applyLeaveId: Int,
            @Field("ApprovedRemarks") strApprovedRemarks: String,
            @Field("LeaveType") leaveType: Int,
            @Field("PaidLeave") strPaidLeave: String,
            @Field("UnpaidLeave") strUnpaidLeave: String,
            @Field("AppliedActualDays") strAppliedDays: String,
            @Field("EmployeeID") employeeID: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<RespondLeavesResponse>


    @FormUrlEncoded
    @POST("leaves/InsertLeaveDisApproval")
    suspend fun disApproveLeaves(
            @Field("UserID") userId: Int,
            @Field("ApplyLeaveID") applyLeaveID: Int,
            @Field("DisApproveRemarks") strDisApproveRemarks: String,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<RespondLeavesResponse>

    @FormUrlEncoded
    @POST("leaves/cancelApplyLeave")
    suspend fun cancelLeave(
            @Field("UserID") userId: Int,
            @Field("ApplyLeaveID") applyLeaveID: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<RespondLeavesResponse>


    @FormUrlEncoded
    @POST("leaves/getLeaveReasons")
    suspend fun leaveReasons(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveReasonsResponse>


    @FormUrlEncoded
    @POST("leaves/getTotalLeavesCount")
    suspend fun getLeaveBalance(
            @Field("UserID") strUserId: Int,
            @Field("LeaveYear") year: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<LeaveBalanceResponse>



    @FormUrlEncoded
    @POST("punchdata/ApplyOD")
    suspend fun applyOD(
            @Field("UserID") userId: Int,
            @Field("FromDate") strStartDate: String,
            @Field("ToDate") strEndDate: String,
            @Field("Sublocation") subLocationId: Int,
            @Field("Remark") strRemarks: String,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>



    // - - - - - Get List of Sub-locations - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getSublocationList")
    suspend fun getSubLocationList(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<SubLocationsResponse>


    // - - - - - Get List of OD's - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getODList")
    suspend fun getODList(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<ODListResponse>


    // - - - - - Get List of OD's For approval (HR & HOD)- - - - - -
    @FormUrlEncoded
    @POST("punchdata/getODForApproval")
    suspend fun getODForApproval(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<ODApprovalResponse>


    // - - - - - Approve/Reject OD's- - - - - -

    //Type - 1 for Approve / 2 for Disapprove

    @FormUrlEncoded
    @POST("punchdata/InsertApprovedDisapprovedOD")
    suspend fun approveRejectOD(
            @Field("UserID") userId: Int,
            @Field("ID") requestId: Int,
            @Field("Type") type: Int,
            @Field("ClientID") strClientId: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>


    // - - - - - Get List of Team - - - - - -
    @FormUrlEncoded
    @POST("leaves/getEmployeeForLeave")
    suspend fun getMyTeam(
            @Field("UserID") strUserId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<MyTeamResponse>


    // - - - - - Get Team Profile Details- - - - - -
    @FormUrlEncoded
    @POST("leaves/getEmployeeLeaveDetails")
    suspend fun getMyTeamProfile(
            @Field("UserID") strUserId: Int,
            @Field("LeaveYear") year: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<MyTeamProfileResponse>

    // - - - - - Get List of All Parties API in accounts detail - - - - - -
    @FormUrlEncoded
    @POST()
    suspend fun getIncreaseLimitParty(
            @Url url : String = "${BASE_URL}GetIncreaseLimitParty",
            @Field("partyid") strPartyId: String,
            @Field("searchtxt") strSearchText: String
    ): Response<List<IncreaseLimitPartyResponse>>

    // - - - - - -  API for getting Aging details - - - - - - - - - - -
    @FormUrlEncoded
    @POST()
    suspend fun getAgingDetail(
            @Url url : String = "${BASE_URL}getAging",
            @Field("CIN") strCin: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<List<AgingResponse>>


    // - - - - - -  API to increase Limit Details - - - - - - - - - - -
    @FormUrlEncoded
    @POST()
    suspend fun updateLimitParty(
            @Url url : String = "${BASE_URL}UpdateIncreaseLimitParty",
            @Field("CIN") strCin: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<List<AgingResponse>>


    // - - - - - -  API for getting increase limit party detail - - - - - - - - - - -
    // - - - - - -  On click of Limit Details Button - - - - - - - - - - -

    @FormUrlEncoded
    @POST()
    suspend fun getIncreaseLimitDetail(
            @Url url : String = "${BASE_URL}GetIncreaseLimitPartyDetails",
            @Field("userid") strPartyId: String,
            @Field("searchtxt") strSearchText: String
    ): Response<List<LimitPartyDetailResponse>>


    // - - - - - -  API for Pie Chart of Attendance ( % ) - - - - - - - - - - -
    @FormUrlEncoded
    @POST("events/getCountOfPresentAbsent")
    suspend fun employeeAttendance(
            @Field("UserID") strUserId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<EmployeeAttendanceResponse>


    // - - - - - -  API for Team Attendance History  - - - - - - - - - - -
    @FormUrlEncoded
    @POST("events/getPresentAbsentEmployeeList")
    suspend fun attendanceHistory(
            @Field("UserID") userId: Int,
            @Field("Typedetails") attendanceStatus: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AttendanceHistoryResponse>

    // - - - - - -  API for Applying Attendance Regularization  - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/insertAttendanceRegularization")
    suspend fun insertAttendanceRegularization(
            @Field("UserID") userId: Int,
            @Field("PunchDate") punchDate: String,
            @Field("OldPunchIn") oldPunchIn: String,
            @Field("OldPunchOut") oldPunchOut: String,
            @Field("NewPunchIn") newPunchIn: String,
            @Field("NewPunchOut") newPunchOut: String,
            @Field("Resaon") reason: String,
            @Field("Remark") remark: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>

    // - - - - - -  API for Regularization Attendance History  - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getAttendanceRegularizeList")
    suspend fun getAttendanceRegularizeList(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AttendanceRegularizationListResponse>



    // - - - - - -  API for Regularization Attendance Approval List - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getAttendanceRegularizeForApproval")
    suspend fun getAttendanceRegularizeForApproval(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<RegularizeAttendanceApprovalResponse>


    // - - - - - -  API for Accept/Reject Regularization Attendance  - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/InsertApprovedDisapprovedAttendanceReg")
    suspend fun insertApprovedDisapprovedAttendanceReg(
            @Field("UserID") userId: Int,
            @Field("ID") uniqueId: Int,
            @Field("ApproveDisApproveRemarks") approveDisApproveRemarks: String,
            @Field("Type") approvalType: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>


    // - - - - - -  API for accepted/rejected records of  Regularization Attendance by admin - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getAttendanceRegularizeByUser")
    suspend fun getAttendanceRegularizeByUser(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<AttendanceRegularizeByUserResponse>


    // - - - - - -  API for cancel  Regularization Request - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/cancelRegulaizationRequest")
    suspend fun cancelRegularizationRequest(
            @Field("UserID") userId: Int,
            @Field("ID") uniqueId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>



    // - - - - - -  API for Notification Feeds - - - - - - - - - - -
    @FormUrlEncoded
    @POST("events/getNotification")
    suspend fun fetchNotifications(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<NotificationFeedsResponse>



    // - - - - - -  API for Pending Leaves For Approval for HR / Manager - - - - - - - - - - -
    @FormUrlEncoded
    @POST("punchdata/getCountOdLeAr")
    suspend fun getPendingRequestsCount(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<RequestsCountResponse>


    // - - - - - -  API for Applying Short Leaves - - - - - - - - - - -
    @FormUrlEncoded
    @POST("leaves/applyShortLeave")
    suspend fun applySL(
            @Field("UserID") userId: Int,
            @Field("Date") strDate: String,
            @Field("NumberOfHrs") noOfHrs: String,
            @Field("Remark") strRemarks: String,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>

    // - - - - - -  API for Applying Short Leaves - - - - - - - - - - -
    @FormUrlEncoded
    @POST("leaves/getShortLeaveDetails")
    suspend fun getSLHistory(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<SLHistoryResponse>

    // - - - - - -  API for  Short Leaves List For Approval - - - - - - - - - - -
    @FormUrlEncoded
    @POST("leaves/getShortLeaveForApproval")
    suspend fun getShortLeaveForApproval(
            @Field("UserID") userId: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<SLForApprovalResponse>


    // - - - - - -  API for  Approve/Reject Short Leaves - - - - - - - - - - -
    @FormUrlEncoded
    @POST("leaves/InsertApprovedDisapprovedForShortLeave")
    suspend fun approveRejectSL(
            @Field("UserID") userId: Int,
            @Field("ID") requestId: Int,
            @Field("Type") Type: Int,
            @Field("ClientID") strClientid: String,
            @Field("ClientSecret") strClientSecret: String
    ): Response<DefaultMessageResponse>

    @FormUrlEncoded
    @POST("common/common-image-upload")
    suspend fun commonImageUpload(
        @Field("ModuleName") moduleName: String,
        @Field("ImageBlob") imgBlob: String
    ): Response<CommonImageUploadResponse>

    @FormUrlEncoded
    @POST("common/add-update-company-details")
    suspend fun addCompanyDetails(
        @Field("CompanyDetailsID") companyDetailsId: Int,
        @Field("AppType") appType: String,
        @Field("UserID") userId: Int,
        @Field("CINNo") cinNo: String,
        @Field("Category") category: String,
        @Field("CompanyName") companyName: String,
        @Field("CompanyAddress") companyAddress: String,
        @Field("VisitingCardImages") visitingCardImages: String,
        @Field("ProductImages") productImages: String,
        @Field("Remark") remark: String,
        @Field("ClientID") strClientid: String,
        @Field("ClientSecret") strClientSecret: String
    ): Response<AddCompanyResponse>

    @FormUrlEncoded
    @POST("common/get-company-details-list")
    suspend fun getCompanyDetails(
        @Field("AppType") appType: String,
        @Field("UserID") userId: Int,
        @Field("CINNo") cinNo: String,
        @Field("Category") category: String,
        @Field("ClientID") strClientid: String,
        @Field("ClientSecret") strClientSecret: String
    ): Response<GetCompanyDetailsResponse>

    @FormUrlEncoded
    @POST("common/delete-company-details")
    suspend fun deleteCompanyDetails(
        @Field("AppType") appType: String,
        @Field("CompanyDetailsID") companyDetailsId: Int,
        @Field("UserID") userId: Int,
        @Field("CINNo") cinNo: String,
        @Field("Remark") remark: String
    ): Response<AddCompanyResponse>

    companion object {
        operator fun invoke(
                networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi {


            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY
            val okkHttpclient = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(networkConnectionInterceptor)
                    .addInterceptor(logging)
                    .build()

            return Retrofit.Builder()
                    .client(okkHttpclient)
                    .baseUrl(HRM_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyApi::class.java)
        }
    }

}

