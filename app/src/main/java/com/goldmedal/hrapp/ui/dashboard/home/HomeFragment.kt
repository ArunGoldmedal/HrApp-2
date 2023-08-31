package com.goldmedal.hrapp.ui.dashboard.home


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.adapters.AnniversaryAdapter
import com.goldmedal.hrapp.data.adapters.BirthdayAdapter
import com.goldmedal.hrapp.data.adapters.HolidayAdapter
import com.goldmedal.hrapp.data.db.entities.AnniversaryData
import com.goldmedal.hrapp.data.db.entities.BirthdayData
import com.goldmedal.hrapp.data.db.entities.EmployeeAttendanceData
import com.goldmedal.hrapp.data.db.entities.HolidayData
import com.goldmedal.hrapp.data.model.*
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.GlobalConstant.ATTENDANCE_SUMMARY
import com.goldmedal.hrapp.databinding.HomeFragmentBinding
import com.goldmedal.hrapp.ui.dashboard.DashboardActivity
import com.goldmedal.hrapp.ui.dashboard.attendance.AttendanceViewModel
import com.goldmedal.hrapp.ui.dashboard.leave.ODRequestsActivity
import com.goldmedal.hrapp.ui.dashboard.notification.NotificationViewModel
import com.goldmedal.hrapp.ui.dialogs.AttendanceHistoryDialog
import com.goldmedal.hrapp.ui.holiday.HolidayListActivity
import com.goldmedal.hrapp.ui.leftdrawer.TimeCardActivity
import com.goldmedal.hrapp.ui.manager.RegularizationRequestsActivity
import com.goldmedal.hrapp.ui.manager.ShortLeavesRequestsActivity
import com.goldmedal.hrapp.ui.map.MapsActivity
import com.goldmedal.hrapp.util.*
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.utils.BannerUtils
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.*
import kotlin.math.roundToInt


const val REFRESH_DASHBOARD = 322

@AndroidEntryPoint
class HomeFragment : Fragment(), ApiStageListener<Any>, View.OnClickListener {


    private val viewModel: HomeViewModel by viewModels()
    private val notiViewModel: NotificationViewModel by viewModels()
    private val attViewModel: AttendanceViewModel by viewModels()


    private lateinit var homeFragmentBinding: HomeFragmentBinding


    var isGeoFenceLock: Boolean? = true
    var officeLatitude: String? = null
    var officeLongitude: String? = null
    var ODSubLocation: String? = null
    var address: String? = null
    var lastCheckInTime: String? = null
    var punchInTime: String? = null

    //Timer

    var millisecondTime: Long = 0L
    var startTime: Long = 0L
    var timeBuff: Long = 0L
    var updateTime: Long = 0L

    var handler: Handler? = null

    var seconds = 0
    var hours = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0


    //Banner
    private lateinit var holidayBanner: BannerViewPager<HolidayData?, BaseViewHolder<HolidayData?>?>
    private lateinit var birthdayBanner: BannerViewPager<BirthdayData?, BaseViewHolder<BirthdayData?>?>
    private lateinit var anniversaryBanner: BannerViewPager<AnniversaryData?, BaseViewHolder<AnniversaryData?>>

    private val runnable = Runnable { formatTimer() }


    private fun formatTimer() {
        millisecondTime = System.currentTimeMillis() - startTime

        updateTime = timeBuff + millisecondTime

        seconds = ((updateTime / 1000).toInt())

        minutes = seconds / 60

        hours = minutes / 60

        seconds %= 60

        milliSeconds = ((updateTime % 1000).toInt())


        val timeLeftFormatted: String =
                TimeDurationUtil.formatHoursMinutesSeconds(millisecondTime)

        text_view_timer?.text = timeLeftFormatted

        handler?.postDelayed(runnable, 1000)
    }


    private fun stopTimer() {
        timeBuff += millisecondTime
        handler?.removeCallbacks(runnable)
        //disableCheckoutButton()
    }

    private fun disableCheckoutButton() {
        btnCheckout?.isEnabled = false
        btnCheckout?.alpha = 0.4f
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return homeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeFragmentBinding.viewmodelHome = viewModel



        birthdayBanner = requireView().findViewById(R.id.birthdayBanner)
        anniversaryBanner = requireView().findViewById(R.id.anniversaryBanner)
        holidayBanner = requireView().findViewById(R.id.holidayBanner)



        viewModel.apiListener = this
        attViewModel.apiListener = this
        notiViewModel.apiListener = this

        btnCheckIn.setOnClickListener(this)
        btnCheckout.setOnClickListener(this)
        llRequests1.setOnClickListener(this)
        llRequests2.setOnClickListener(this)
        llRequests3.setOnClickListener(this)
        llRequests4.setOnClickListener(this)
        btn_reg.setOnClickListener(this)

        //Attendance Details Block - - - - - - - - -
        getCheckedChip()



        viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                notiViewModel.fetchNotifications(user.UserID)
                viewModel.upcomingHolidays(user.UserID)
                viewModel.upcomingBirthdays(user.UserID)
                viewModel.upcomingAnniversary(user.UserID)

                if (user.ISHr == 1 || user.IsReportingPerson == 1) {
                    viewHr?.visibility = View.VISIBLE
                    viewModel.employeeAttendance(user.UserID)
                    viewModel.getPendingRequestsCount(user.UserID)
                }
            }
        })

        initBanner()


        handler = Handler()

    }

    /*
    * Displays Holiday,Birthday & Anniversary Banners
    */
    private fun initBanner() {
        activity.let {
            holidayBanner.apply {
                setScrollDuration(800)
                setLifecycleRegistry(lifecycle)
                setIndicatorStyle(IndicatorStyle.CIRCLE)
                setIndicatorSliderGap(resources.getDimensionPixelOffset(R.dimen.dp_4))
                setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_10).toInt())
                setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
                setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_4).toInt(), resources.getDimension(R.dimen.dp_6).toInt())
                setInterval(9000)
                setIndicatorGravity(IndicatorGravity.END)
                disallowInterceptTouchEvent(true)
                setIndicatorView(indicator_view)
                val checkedWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                val normalWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                setIndicatorSliderColor(getColor(requireActivity(), R.color.red_normal_color), getColor(requireActivity(), R.color.red_checked_color))
                setIndicatorSliderWidth(normalWidth, checkedWidth)
                setPageMargin(resources.getDimensionPixelOffset(R.dimen.dp_10))
                setRevealWidth(0, resources.getDimensionPixelOffset(R.dimen.dp_32))

                adapter = HolidayAdapter(resources.getDimensionPixelOffset(R.dimen.dp_8))
                removeDefaultPageTransformer()

                //setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        BannerUtils.log("position:$position")

                        val bannerData: HolidayData? = holidayBanner.data[position]
                        tv_holiday_title?.text = bannerData?.HolidayName

                        if (bannerData?.FromDateFormat.equals(bannerData?.ToDateFormat)) {
                            tv_holiday_date?.text = bannerData?.FromDateFormat
                        } else {
                            tv_holiday_date?.text = bannerData?.FromDateFormat + " - " + bannerData?.ToDateFormat
                        }
                    }
                })

                setOnPageClickListener {
                    activity?.let {
                        val intent = Intent(it, HolidayListActivity::class.java)
                        it.startActivity(intent)
                    }

                }
            }
                    .create()


            birthdayBanner.apply {
                setScrollDuration(800)
                setLifecycleRegistry(lifecycle)
                setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                setIndicatorSliderGap(resources.getDimensionPixelOffset(R.dimen.dp_4))
                setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_10).toInt())
                setIndicatorSlideMode(IndicatorSlideMode.WORM)
                setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_4).toInt(), resources.getDimension(R.dimen.dp_6).toInt())
                setInterval(3000)
                val checkedWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                val normalWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                setIndicatorSliderColor(getColor(requireActivity(), R.color.red_normal_color), getColor(requireActivity(), R.color.red_checked_color))
                setIndicatorSliderWidth(normalWidth, checkedWidth)
                setPageMargin(resources.getDimensionPixelOffset(R.dimen.dp_16))
                setRevealWidth(resources.getDimensionPixelOffset(R.dimen.dp_32))
                adapter = BirthdayAdapter(resources.getDimensionPixelOffset(R.dimen.dp_8))

                //For Two Side Page Reveal
                removeDefaultPageTransformer()
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        BannerUtils.log("position:$position")
                    }
                })
            }
                    .create()


            anniversaryBanner.apply {
                setScrollDuration(800)
                setLifecycleRegistry(lifecycle)
                setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                setIndicatorSliderGap(resources.getDimensionPixelOffset(R.dimen.dp_4))
                setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_10).toInt())
                setIndicatorSlideMode(IndicatorSlideMode.WORM)
                setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_4).toInt(), resources.getDimension(R.dimen.dp_6).toInt())
                setInterval(5000)
                val checkedWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                val normalWidth = resources.getDimensionPixelOffset(R.dimen.dp_10)
                setIndicatorSliderColor(getColor(requireActivity(), R.color.red_normal_color), getColor(requireActivity(), R.color.red_checked_color))
                setIndicatorSliderWidth(normalWidth, checkedWidth)
                setPageMargin(resources.getDimensionPixelOffset(R.dimen.dp_10))
                setRevealWidth(resources.getDimensionPixelOffset(R.dimen.dp_20))

                adapter = AnniversaryAdapter(resources.getDimensionPixelOffset(R.dimen.dp_8))

                //For Two Side Page Reveal
                removeDefaultPageTransformer()
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        BannerUtils.log("position:$position")
                    }
                })
            }
                    .create()
        }
    }

    /**
     * Returns Attendance Details of Last 7 Days,Prev & Curr Month
     */
    private fun getCheckedChip(){
        homeFragmentBinding.chipGroupChoice.setOnCheckedChangeListener { group, checkedId ->
            // Responds to child chip checked/unchecked
            viewModel.getLoggedInUser().observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    when (checkedId) {
                        R.id.chipLast7Days -> {
                            last7DaysAttendance()
                            loadAttendanceDetails(user.UserID)

                        }
                        R.id.chipCurrMonth -> {
                            setCurrentMonthAttendance()
                            loadAttendanceDetails(user.UserID)

                        }
                        R.id.chipPrevMonth -> {
                            setPreviousMonthAttendance()
                            loadAttendanceDetails(user.UserID)
                        }
                    }
                }
            }


        }
    }



    private fun loadAttendanceDetails(userID: Int?) {
        viewEmployee?.visibility = View.VISIBLE
//        val calendar = Calendar.getInstance()
//        val today = calendar.time
//
//        calendar.add(Calendar.DAY_OF_MONTH, -6)
//        val previous = calendar.time
//
//        attViewModel.strStartDate = previous.toString("yyyy-MM-dd")
//        attViewModel.strEndDate = today.toString("yyyy-MM-dd")

        //attViewModel.getAttendanceDetails(userID)
        attViewModel.getAttendanceSummary(userID)
    }

    private fun last7DaysAttendance(){
        val calendar = Calendar.getInstance()
        val today = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6)
        val previous = calendar.time

        attViewModel.strStartDate = previous.toString("yyyy-MM-dd")
        attViewModel.strEndDate = today.toString("yyyy-MM-dd")


        homeFragmentBinding.txtAttendanceHeader.text = "${previous.toString("dd MMM yyyy")} - ${today.toString("dd MMM yyyy")}"



    }

    /**
     * Returns Attendance Details of Previous Month
     */
    private fun setPreviousMonthAttendance(){
        val calendar = Calendar.getInstance()

        //val today = calendar.time

        //Last Month Date as Start date
        calendar.add(Calendar.MONTH, -1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val previous = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
//read it
       // calendar.add(Calendar.MONTH, -1)
        val today = calendar.time



        attViewModel.strStartDate = previous.toString("yyyy-MM-dd")
        attViewModel.strEndDate = today.toString("yyyy-MM-dd")

        homeFragmentBinding.txtAttendanceHeader.text = "${previous.toString("dd MMM yyyy")} - ${today.toString("dd MMM yyyy")}"
    }

    private fun setCurrentMonthAttendance(){

        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1);
      //  calendar.add(Calendar.MONTH, -1)
        val previous = calendar.time

        attViewModel.strStartDate = previous.toString("yyyy-MM-dd")
        attViewModel.strEndDate = today.toString("yyyy-MM-dd")


        homeFragmentBinding.txtAttendanceHeader.text = "${previous.toString("dd MMM yyyy")} - ${today.toString("dd MMM yyyy")}"
    }



    override fun onStart() {
        super.onStart()

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                viewModel.punchInoutStatus(user.UserID)
                //By Default last 7 Days to be shown

                homeFragmentBinding.chipGroupChoice.check(R.id.chipLast7Days)
               last7DaysAttendance()
               loadAttendanceDetails(user.UserID)

            }
        })


    }

    override fun onStop() {
        super.onStop()
        handler?.removeCallbacks(runnable)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFRESH_DASHBOARD) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
                    if (user != null) {
                        homeFragmentBinding.chipGroupChoice.check(R.id.chipLast7Days)
                        last7DaysAttendance()
                        loadAttendanceDetails(user.UserID)
                    }
                })
            }
        }
    }

    override fun onStarted(callFrom: String) {
        if (callFrom == "holidays") {
            holidays_progress_bar?.start()
        }
        if (callFrom == "birthday") {
            birthday_progress_bar?.start()
        }
        if (callFrom == "anniversary") {
            anniversary_progress_bar?.start()
        }
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        Coroutines.main {

            if (callFrom == "notification_feeds") {
                val data = _object as ArrayList<NotificationFeeds?>
                if (!data.isNullOrEmpty()) {
                    viewAnnouncements?.visibility = View.VISIBLE

                    for (feeds in data) {
                        txt_announcement?.append("     \u2022     ${feeds?.Body}")
                    }

                    val animBlink = AnimationUtils.loadAnimation(requireContext(),
                            R.anim.blink)
                    txt_announcement?.startScroll()

                    // load the animation
                    txt_announcement?.startAnimation(animBlink)


                }
            }

            if (callFrom.equals("punchStatus")) {

                val punchStatusData = _object as List<PunchInOutStatusData>
                isGeoFenceLock = punchStatusData[0].IsGeoFenceLock
                officeLatitude = punchStatusData[0].OfficeLat
                officeLongitude = punchStatusData[0].OfficeLong
                ODSubLocation = punchStatusData[0].ODSublocation
                address = punchStatusData[0].Address
                punchInTime = punchStatusData[0].PunchIn
                lastCheckInTime = punchStatusData[0].LastCheckIn

                when (punchStatusData[0].Status) {
                    true -> {
                        showCheckoutView()
                    }
                    false -> {
                        viewModel.getLoggedInUser().observe(this) { user ->
                            if (user != null) {
                                if (user.IsExecutive == 0) {
                                    showCheckInView()
                                }
                            }
                        }
                    }
                    else -> {
                        viewModel.getLoggedInUser().observe(this) { user ->
                            if (user != null) {
                                if (user.IsExecutive == 0) {
                                    showCheckInView()
                                }
                            }
                        }
                    }
                }

            }
            if (callFrom.equals("holidays")) {
                holidays_progress_bar?.stop()
                val data = _object as MutableList<HolidayData?>
                if (data.isEmpty()) {

                    val noData = HolidayData()
                    noData.ViewType = GlobalConstant.TYPE_NO_DATA
                    data.add(noData)
                } else {
                    layout_indicator?.visibility = View.VISIBLE

                }
                holidayBanner.refreshData(data)
            }
            if (callFrom.equals("birthday")) {

                birthday_progress_bar?.stop()

                val data = _object as MutableList<BirthdayData?>

                if (data.isEmpty()) {
                    val noData = BirthdayData()
                    noData.Message = "No Upcoming Birthday This Week"
                    noData.ViewType = GlobalConstant.TYPE_NO_DATA
                    data.add(noData)
                }
                birthdayBanner.refreshData(data)
            }

            if (callFrom == "anniversary") {
                anniversary_progress_bar?.stop()


                val data = _object as MutableList<AnniversaryData?>

                if (data.isEmpty()) {

                    val noData = AnniversaryData()
                    noData.Message = "No Upcoming Anniversary This Week"
                    noData.ViewType = GlobalConstant.TYPE_NO_DATA
                    data.add(noData)
                }
                anniversaryBanner.refreshData(data)
            }

            if (callFrom.equals("employee_attendance")) {
                updateChart(_object as List<EmployeeAttendanceData?>)
            }


            if (callFrom == "pending_requests_cnt") {
                bindPendingRequestsCount(_object as List<RequestsCountData?>?)
            }
//            if (callFrom == "team_attendance") {
//                val data = _object as List<AttendanceDetailsData?>
//                bindWorkingHours(data)
//                val presentCount = data.filter {
//                    (it?.status.equals(AttendanceFragment.TAG_PRESENT, ignoreCase = true))
//                }
//
//                val absentCount = data.filter {
//                    (it?.status.equals(AttendanceFragment.TAG_ABSENT, ignoreCase = true))
//                }
//
//                val missedPunchCount = data.filter {
//                    (it?.status.equals(AttendanceFragment.TAG_CHECKOUT_MISSING, ignoreCase = true))
//                }
//
//                txt_present_days?.text = presentCount.size.toString()
//                txt_absent_days?.text = absentCount.size.toString()
//                txt_missed_punch_days?.text = missedPunchCount.size.toString()
//            }




            if (callFrom == ATTENDANCE_SUMMARY) {
                val data = _object as List<AttendanceSummaryData?>

                val summaryData = data[0]

                txt_total_days?.text = formatNumber(summaryData?.TotalDays.toString())
                txt_present_days?.text = formatNumber(summaryData?.PresentDays.toString())
                txt_absent_days?.text = formatNumber(summaryData?.AbsentDays.toString())
                txt_missed_punch_days?.text = formatNumber(summaryData?.punchoutmissing.toString())
                txt_open_leaves?.text = formatNumber(summaryData?.Openleave.toString())
                txt_approved_leaves?.text = formatNumber(summaryData?.Approvedleave.toString())
                txt_company_holiday?.text = formatNumber(summaryData?.CompanyHoliday.toString())
                txt_weekend?.text = formatNumber(summaryData?.WeekendDays.toString())
            }
        }
    }





    private fun formatWorkingHours(hours: Int, min: Int): String {
        return String.format("%02d:%02d", hours, ((min.toDouble() * 60) / 100).roundToInt())
    }

    private fun bindPendingRequestsCount(list: List<RequestsCountData?>?) {

        list?.let {

            viewRequests?.visibility = View.VISIBLE

            for (i in list.indices) {
                when (list[i]?.ModuleId) {
                    1 -> {
                        txt_header_1?.text = list[i]?.ModuleName
                        tv_count_1?.text = list[i]?.CountData?.toString() ?: "0"
                        imv_req_1?.setImageResource(R.drawable.reg_req)
                    }
                    2 -> {
                        txt_header_2?.text = list[i]?.ModuleName
                        tv_count_2?.text = list[i]?.CountData?.toString() ?: "0"
                        imv_req_2?.setImageResource(R.drawable.od_req)
                    }
                    3 -> {
                        txt_header_3?.text = list[i]?.ModuleName
                        tv_count_3?.text = list[i]?.CountData?.toString() ?: "0"
                        imv_req_3?.setImageResource(R.drawable.leave_req)
                    }
                    4 -> {
                        txt_header_4?.text = list[i]?.ModuleName
                        tv_count_4?.text = list[i]?.CountData?.toString() ?: "0"
                        imv_req_4?.setImageResource(R.drawable.sl_req)
                    }
                    else -> {
                    }
                }
            }
        }


    }


    private fun showCheckoutView() {
        viewCheckout?.visibility = View.VISIBLE
        viewCheckIn?.visibility = View.GONE

        val lastPunchTime = lastCheckInTime?.let { formatDateString(it, "MM/dd/yyyy hh:mm:ss a", "dd/MM/yyyy hh:mm:ss a") }

        if (punchInTime.equals(lastCheckInTime)) {
            startTimer()
            txtPunchTime?.text = "Your last check-in was: ${lastPunchTime}"
        } else {
            stopTimer()
            calculateWorkingHrs()
            txtPunchTime?.text = "Your last check-out was: ${lastPunchTime}"
        }
        viewModel.getLoggedInUser().observe(this,
                { user ->
            if (user != null) {
                if (user.IsExecutive == 1) {
                    btnCheckout?.visibility = View.GONE
                }

            }
        })
    }


    private fun showCheckInView() {

        viewCheckIn?.visibility = View.VISIBLE
        viewCheckout?.visibility = View.GONE

        val calendar = Calendar.getInstance()

        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

        textViewDate?.text = currentDate.toString()
        textViewMonth?.text = currentMonth
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (callFrom == "holidays") {
            holidays_progress_bar?.stop()



            viewModel.getHolidayDataDetail().observe(this, { data ->
                print("outside holiday- - - " + data.size)
                if (data == null) {
                    viewModel.getLoggedInUser().observe(this, { user ->
                        if (user != null) {
                            viewModel.upcomingHolidays(user.UserID)
                        }
                    })
                } else {

                    val holidayData = data as MutableList<HolidayData?>

                    if (holidayData.isEmpty()) {

                        val noData = HolidayData()
                        //noData.Message = "No Upcoming BirthDay This Week"
                        noData.ViewType = GlobalConstant.TYPE_NO_DATA
                        holidayData.add(noData)
                    } else {
                        layout_indicator?.visibility = View.VISIBLE
                    }
                    holidayBanner.refreshData(holidayData)

                }
            })
        }


        if (callFrom == "birthday") {

            birthday_progress_bar?.stop()

            viewModel.getBirthDataDetail().observe(this, { data ->
                print("outside - - - " + data.size)
                Log.d("Outside", "Msg - - - -" + data.size)
                if (data == null) {
                    viewModel.getLoggedInUser().observe(this, { user ->
                        if (user != null) {
                            viewModel.upcomingBirthdays(user.UserID)
                        }
                    })
                } else {
                    val birthdayData = data as MutableList<BirthdayData?>

                    if (birthdayData.isEmpty()) {

                        val noData = BirthdayData()
                        noData.Message = "No Upcoming Birthday This Week"
                        noData.ViewType = GlobalConstant.TYPE_NO_DATA
                        birthdayData.add(noData)
                    }

                    birthdayBanner.refreshData(birthdayData)

                }
            })
        }

        if (callFrom == "employee_attendance") {
            viewModel.getEmployeeAttendanceData().observe(this, { data ->
                if (data == null) {
                    viewModel.getLoggedInUser().observe(this,  { user ->
                        if (user != null) {
                            viewModel.employeeAttendance(user.UserID)
                        }
                    })
                } else {
                    updateChart(data)

                }
            })
        }


        if (callFrom == "anniversary") {

            anniversary_progress_bar?.stop()

            viewModel.getAnniversaryDataDetail().observe(this,  { data ->
                print("outside anniversary- - - " + data.size)
                Log.d("Outside anniversary", "Msg - - - -" + data.size)
                if (data == null) {
                    viewModel.getLoggedInUser().observe(this,  { user ->
                        if (user != null) {
                            viewModel.upcomingAnniversary(user.UserID)
                        }
                    })
                } else {
                    val anniversaryData = data as MutableList<AnniversaryData?>
                    if (anniversaryData.isEmpty()) {
                        val noData = AnniversaryData()
                        noData.Message = "No Upcoming Anniversary This Week"
                        noData.ViewType = GlobalConstant.TYPE_NO_DATA
                        anniversaryData.add(noData)
                    }
                    anniversaryBanner.refreshData(anniversaryData)

                }
            })
        }

    }

    override fun onClick(v: View?) {
        val id = v?.id

        if (id == R.id.btnCheckIn) {
            showMap("IN")
        } else if (id == R.id.btnCheckout) {
            showMap("OUT")
        } else if (id == R.id.llRequests1) {
            RegularizationRequestsActivity.start(requireContext())
        } else if (id == R.id.llRequests2) {
            ODRequestsActivity.start(requireContext())
        } else if (id == R.id.llRequests3) {
            val _context = activityContext(context) as? DashboardActivity
            _context?.showRequestsFragment()
        } else if (id == R.id.llRequests4) {
            ShortLeavesRequestsActivity.start(requireContext())
        }
        else if (id == R.id.btn_reg) {
            TimeCardActivity.start(requireContext())
        }
    }

    private fun showMap(punchType: String) {
//        activity?.let {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            intent.putExtra("punchType", punchType)
            intent.putExtra("isGeoFenceLock", isGeoFenceLock)
            intent.putExtra("officeLatitude", officeLatitude)
            intent.putExtra("officeLongitude", officeLongitude)
            intent.putExtra("ODSubLocation", ODSubLocation)
            intent.putExtra("Address", address)

//            intent.putExtra("officeLatitude", "19.1803381")    //19.1784034
//            intent.putExtra("officeLongitude", "72.8382487")  //  72.8316975

            startActivityForResult(intent,REFRESH_DASHBOARD)
//        }
    }


    private fun calculateWorkingHrs() {

        val punchTime = punchInTime?.let { getDateFromString(it, "MM/dd/yyyy hh:mm:ss a") }
        val punchInMilliseconds = punchTime?.time ?: 0


        val lastCheckIn = lastCheckInTime?.let { getDateFromString(it, "MM/dd/yyyy hh:mm:ss a") }
        val lastCheckInMilliseconds = lastCheckIn?.time ?: 0


        val timeDiffMilliSecs = lastCheckInMilliseconds - punchInMilliseconds

        val timeLeftFormatted: String =
                TimeDurationUtil.formatHoursMinutesSeconds(timeDiffMilliSecs)

        text_view_timer?.text = timeLeftFormatted


    }

    private fun startTimer() {
        val punchTime = punchInTime?.let { getDateFromString(it, "MM/dd/yyyy hh:mm:ss a") }
        startTime = punchTime?.time ?: 0

        handler?.postDelayed(runnable, 0)
    }


    /*  - - - - - - - - - - - - -   HR Analytics - - - - - - - - - - - -  */

    private fun updateChart(list: List<EmployeeAttendanceData?>) {

        if (list.isNotEmpty()) {
            if (isAdded) {
                val view = layoutInflater.inflate(R.layout.pie_chart_layout, null)

                hr_analytics_layout?.addView(view)


                val layoutPresent = view?.findViewById<ConstraintLayout>(R.id.present_constraintLayout)
                val layoutAbsent = view?.findViewById<ConstraintLayout>(R.id.absent_constraintLayout)


                // Update the text in a center of the chart:
                val txtPresentPercent = view?.findViewById<TextView>(R.id.txt_present_percent)
                val txtAbsentPercent = view?.findViewById<TextView>(R.id.txt_absent_percent)

                val txtPresentCount = view?.findViewById<TextView>(R.id.txt_present_count)
                val txtAbsentCount = view?.findViewById<TextView>(R.id.txt_absent_count)


                val presentCount = list[0]?.PresentCount ?: 0
                val absentCount = list[0]?.AbsentCount ?: 0
                val totalCount = list[0]?.TotalEmployee ?: 0


                // Calculate the slice size and update the pie chart:
                val presentPieChart = view?.findViewById<ProgressBar>(R.id.present_stats_progressbar)
                val absentPieChart = view?.findViewById<ProgressBar>(R.id.absent_stats_progressbar)

                val presentPercent = calculatePercentage(presentCount.toDouble(), totalCount.toDouble())
                val absentPercent = calculatePercentage(absentCount.toDouble(), totalCount.toDouble())

                txtPresentCount?.text = presentCount.toString()
                txtAbsentCount?.text = absentCount.toString()

                txtPresentPercent?.text = (presentPercent).toString() + " % "
                txtAbsentPercent?.text = (absentPercent).toString() + " % "

                if (presentPercent == 0) {
                    presentPieChart?.progress = 1
                } else {
                    val anim = ProgressBarAnimation(presentPieChart, 0f, presentPercent.toFloat())
                    anim.duration = 1500
                    presentPieChart?.startAnimation(anim)

                }

                if (absentPercent == 0) {
                    absentPieChart?.progress = 1
                } else {
                    val anim = ProgressBarAnimation(absentPieChart, 0f, absentPercent.toFloat())
                    anim.duration = 2000
                    absentPieChart?.startAnimation(anim)

                }


                layoutPresent?.setOnClickListener {
                    showAttendanceDialog(2)
                }


                layoutAbsent?.setOnClickListener {
                    showAttendanceDialog(3)
                }

            }
        }

    }

    private fun showAttendanceDialog(attendanceStatus: Int) {

        val dialogFragment = AttendanceHistoryDialog.newInstance()

        val bundle = Bundle()
        bundle.putInt("attendanceStatus", attendanceStatus)
        dialogFragment.arguments = bundle
        val _context = activityContext(context) as? AppCompatActivity
        val ft = _context?.supportFragmentManager?.beginTransaction()
        val prev = _context?.supportFragmentManager?.findFragmentByTag("attendance_dialog")
        if (prev != null) {
            ft?.remove(prev)
        }
        ft?.addToBackStack(null)
        ft?.let { dialogFragment.show(it, "attendance_dialog") }

    }


    private fun calculatePercentage(value: Double, totalValue: Double): Int {
        return ((value / totalValue) * 100).roundToInt()
    }

    override fun onValidationError(message: String, callFrom: String) {}
}

