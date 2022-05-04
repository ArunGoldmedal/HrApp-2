package com.goldmedal.hrapp.ui.dashboard.attendance

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.HexColors
import com.goldmedal.hrapp.data.CalendarObj
import com.goldmedal.hrapp.data.adapters.CustomLegendAdapter
import com.goldmedal.hrapp.data.db.entities.GetAllAttendanceData
import com.goldmedal.hrapp.data.db.entities.GetCurrentAttendanceData
import com.goldmedal.hrapp.databinding.AttendanceFragmentBinding
import com.goldmedal.hrapp.util.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.attendance_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AttendanceFragment : Fragment(),  OnDateSelectedListener, ApiStageListener<Any> {




private val viewModel: AttendanceViewModel by viewModels()

    var widget: MaterialCalendarView? = null
    private lateinit var attFragmentBinding: AttendanceFragmentBinding

    private lateinit var adapter: CustomLegendAdapter
    private var attendanceData: List<GetAllAttendanceData>? = null
    private var currentAttendanceData: List<GetCurrentAttendanceData>? = null
    private var minimumDate: Long = 0
    private var maximumDate: Long = 0


    companion object {
         const val TAG_ABSENT = "ABSENT"
        const val TAG_PRESENT = "PRESENT"
         const val TAG_HALF_DAY = "HALF DAY"
         const val TAG_HOLIDAY = "HOLIDAY"
         const val TAG_WEEKEND = "WEEKEND"
         const val TAG_CHECKOUT_MISSING = "CHECKOUT MISSING"

         const val TAG_PUNCH_TYPE_MOBILE  = "MOBILE"
         const val TAG_CHECKOUT_BIOMETRIC = "BIOMETRIC"
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        attFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.attendance_fragment, container, false)
        return attFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attFragmentBinding.viewmodel = viewModel

        viewModel.apiListener = this
        widget = view?.findViewById(R.id.calendarView)

        widget?.setOnDateChangedListener(this)
        widget?.showOtherDates = MaterialCalendarView.SHOW_ALL


        val instance = LocalDate.now()
        widget?.setSelectedDate(instance)

        val min = LocalDate.of(instance.year - 2, Month.JANUARY, 1)
        val max = LocalDate.of(instance.year, Month.DECEMBER, 31)

        minimumDate = (instance.year - 2).toLong()
        maximumDate = (instance.year).toLong()


        val formatDate = formatDateString(instance.toString(),"yyyy-MM-dd","dd/MM/yyyy")
        textViewDate?.text =  formatDate

        widget?.state()?.edit()?.setMinimumDate(min)?.setMaximumDate(max)?.commit()

        widget?.addDecorators(
                HighlightWeekendsDecorator()
        )
        widget?.invalidateDecorators()


        val calendar = Calendar.getInstance()
        val today = calendar.time

        calendar.add(Calendar.YEAR, -2)
        val previous = calendar.time



        viewModel.strStartDate = previous.toString("yyyy-MM-dd")
        viewModel.strEndDate = today.toString("yyyy-MM-dd")



        val attInfo = arrayListOf<String>()
        attInfo.add("Present")
        attInfo.add("Absent")
        attInfo.add("Half Day")
        attInfo.add("Missed Punch")
        attInfo.add("Holiday")

        val colors = arrayListOf<HexColors>()
        colors.add(HexColors("#228B22")) //Green
        colors.add(HexColors("#AD160F")) //Red
        colors.add(HexColors("#2577E7")) //Blue
        colors.add(HexColors("#ff9922")) //Yellow
        colors.add(HexColors("#ffaa66cc")) //Purple

        adapter = CustomLegendAdapter(colors, attInfo)
        recyclerView_legends.adapter = adapter
        recyclerView_legends.layoutManager = GridLayoutManager(activity, 3)

        Coroutines.main {

            viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    viewModel.userId = user.UserID
                    textViewEmployeeName?.text = user.EmployeeFullName


                    val avatar = if (user.Genderid.equals("1")) R.drawable.male_avatar else R.drawable.female_avatar

                    Glide.with(this)
                            .load(user.ProfilePicture)
                            .fitCenter()
                            .placeholder(avatar)
                            .into(imgProfilePic)



                    Coroutines.main {
                        progress_bar?.start()
                        val attendance = viewModel.attendanceData.await()

                 //If used viewLifecycleOwner app crashes ,let context be this
                        attendance.observe(this, Observer { it ->


                            progress_bar?.stop()
                            if (it != null) {
                                attendanceData = it
                                Coroutines.main {

                                    setDecorator()
                                    attendanceData?.let { setPreviousAddress(it, CalendarDay.today().date) }
                                }
                            }
                        })
                    }
                }
            })
        }


        textViewDate.setOnClickListener {
            val c = Calendar.getInstance()
            val c1 = Calendar.getInstance()

            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->

                        val selectedDate = CalendarDay.from(year, monthOfYear + 1, dayOfMonth)
                        calendarView.selectedDate = selectedDate
                        calendarView.setCurrentDate(selectedDate, true)



                        val formattedSelectedDate = formatDateString(selectedDate.date.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                        textViewDate.text = formattedSelectedDate

                        if (selectedDate.equals(CalendarDay.today())) {
                            Log.d("same date - -- ", "" + selectedDate + " -- - " + CalendarDay.today())
                            viewModel.currentAttendance()
                        } else {
                            Log.d("diff date - -- ", "" + selectedDate + " -- - " + CalendarDay.today())
                            attendanceData?.let { setPreviousAddress(it, selectedDate.date) }
                        }

                    }, mYear, mMonth, mDay)


            Log.d("min max date - - - ", " -- - " + (mYear - 2).toLong() + " - - - " + (mYear).toLong())

            c.set(Calendar.MONTH, 0)
            c.set(Calendar.DAY_OF_MONTH, 1)
            c.set(Calendar.YEAR, mYear - 2)
            datePickerDialog.datePicker.minDate = c.timeInMillis

            c1.set(Calendar.MONTH, 11)
            c1.set(Calendar.DAY_OF_MONTH, 31)
            c1.set(Calendar.YEAR, mYear)
            datePickerDialog.datePicker.maxDate = c1.timeInMillis


            datePickerDialog.show()
        }
    }


    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }



    override fun onSuccess(_object: List<Any?>, callFrom: String) {


        progress_bar?.stop()
        if (callFrom.equals("today_attendance")) {
            if (_object != null) {
                Log.d("object current", "" + _object.size)
                currentAttendanceData = _object as? List<GetCurrentAttendanceData>
                currentAttendanceData?.let {

                    setCurrentAddress(it)


                }
            }
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar.stop()

        if (callFrom.equals("today_attendance")) {
            viewModel.getCurrentAttendanceDataDetail().observe(this, Observer { data ->
                print("today_attendance- - - " + data.size)
                Log.d("today_attendance", "Msg - - - -" + data.size)

                if (data.isNullOrEmpty()) {
                } else {
                    setCurrentAddress(data)
                }

            })


            if(isNetworkError){
                root_layout?.snackbar(message)
            }
        }

    }


    private fun setCurrentAddress(currentAttendanceData: List<GetCurrentAttendanceData?>) {
activity?.let {
    widget?.addDecorators(CurrentDayDecorator(activity, CalendarDay.today(), currentAttendanceData[0]?.status ?: TAG_HOLIDAY))
}



        txt_punch_time?.text = currentAttendanceData[0]?.FirstIn
        txt_punch_out_time?.text = currentAttendanceData[0]?.LastOut


        //Check First PunchType
        if (currentAttendanceData[0]?.FirstInPunchType.equals(TAG_PUNCH_TYPE_MOBILE, ignoreCase = true)) {
            txt_punch_location?.text = getAddressFromLatLong(context, currentAttendanceData[0]?.FirstInLatitude?.toDoubleOrNull() ?: 0.0,
                    currentAttendanceData[0]?.FirstInLongitude?.toDoubleOrNull() ?: 0.0)
        }else{
            txt_punch_location?.text = currentAttendanceData[0]?.FirstInLocation
        }


        //Check Last PunchType
        if (currentAttendanceData[0]?.LastOutPunchType.equals(TAG_PUNCH_TYPE_MOBILE, ignoreCase = true)) {
            txt_punch_out_location?.text = getAddressFromLatLong(context, currentAttendanceData[0]?.LastOutLatitude?.toDoubleOrNull() ?: 0.0,
                    currentAttendanceData[0]?.LastOutLongitude?.toDoubleOrNull() ?: 0.0)
        }else{
            txt_punch_out_location?.text = currentAttendanceData[0]?.LastOutLocation
        }

        txt_working_hrs?.text = currentAttendanceData[0]?.TotalHours + " of working hours"
        scrollView?.post {
            scrollView?.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun setPreviousAddress(prevAttendanceData: List<GetAllAttendanceData?>, selectedDate: LocalDate) {
        val parser = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        val formatter = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
        val formattedDate = formatter.format(parser.parse(selectedDate.toString()))
        Log.d("formattedDate - - - ", "" + formattedDate)


        val prevAttendanceObj = prevAttendanceData.filter {

            (it?.DisplayDate ?: "-").contains(formattedDate.toString())
        }
        Log.d("prev - - - ", "" + prevAttendanceObj)

        if (prevAttendanceObj.isNullOrEmpty()) {
            txt_punch_time.text = "-"
            txt_punch_out_time.text = "-"

            txt_punch_location.text = "-"
            txt_punch_out_location.text = "-"


        } else {
            txt_punch_time.text =  prevAttendanceObj[0]?.FirstIn
            txt_punch_out_time.text =  prevAttendanceObj[0]?.LastOut



            //Check First PunchType
            if (prevAttendanceObj[0]?.FirstInPunchType.equals(TAG_PUNCH_TYPE_MOBILE, ignoreCase = true)) {
                txt_punch_location.text = getAddressFromLatLong(context, prevAttendanceObj[0]?.FirstInLatitude?.toDoubleOrNull() ?: 0.0,
                        prevAttendanceObj[0]?.FirstInLongitude?.toDoubleOrNull() ?: 0.0)
            }else{
                txt_punch_location.text = prevAttendanceObj[0]?.FirstInLocation
            }


            //Check Last PunchType
            if (prevAttendanceObj[0]?.LastOutPunchType.equals(TAG_PUNCH_TYPE_MOBILE, ignoreCase = true)) {
                txt_punch_out_location.text = getAddressFromLatLong(context, prevAttendanceObj[0]?.LastOutLatitude?.toDoubleOrNull() ?: 0.0,
                        prevAttendanceObj[0]?.LastOutLongitude?.toDoubleOrNull() ?: 0.0)
            }else{
                txt_punch_out_location.text = prevAttendanceObj[0]?.LastOutLocation
            }

            txt_working_hrs.text = prevAttendanceObj[0]?.TotalHours + " of working hours"
        }



    }


    override fun onDateSelected(
            widget: MaterialCalendarView,
            date: CalendarDay,
            selected: Boolean) {
        //If you change a decorate, you need to invalidate decorators


        widget.invalidateDecorators()
        Log.d("---", "----")

        val formatDate = formatDateString(date.date.toString(),"yyyy-MM-dd","dd/MM/yyyy")
        textViewDate.text = formatDate

        //  - - - - - - Hit api for current attendance data - - - - - -
        if (date.equals(CalendarDay.today())) {
            Log.d("same date - -- ", "" + date + " -- - " + CalendarDay.today())
            viewModel.currentAttendance()
        } else {
            Log.d("diff date - -- ", "" + date + " -- - " + CalendarDay.today())
            attendanceData?.let {

                setPreviousAddress(it, date.date)
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }




    }


    private suspend fun setDecorator() {
        //CPU intensive task so run it on the Default dispatcher
        withContext(Dispatchers.Default) {
            val calendarDays = ArrayList<CalendarObj>()

            if ((attendanceData?.size ?: 0) > 0) {
                for (i in 0..(attendanceData?.size!! - 1)) {
                    val obj = CalendarObj()

                    var current: String?
                    current = attendanceData!![i].DisplayDate
                    Log.d("Current - - - ", "____" + current)

                    if (current.isNullOrEmpty()) {
                        current = "10-01-2020"
                    }

                    val dateParts = current.split("-")


                    obj.days = dateParts[0].toInt()
                    obj.month = dateParts[1].toInt()
                    obj.year = dateParts[2].toInt()
                    obj.status = attendanceData!![i].status
                    Log.d("date obj  - - - -", "- - - " + obj.days + "- - - -" + obj.month + "- - - -" + obj.year + " - - - - " + obj.status)
                    calendarDays.add(obj)
                }
            }


            val calAbsent: MutableList<CalendarDay> = ArrayList()
            val calPresent: MutableList<CalendarDay> = ArrayList()
            val halfDay: MutableList<CalendarDay> = ArrayList()
            val holiday: MutableList<CalendarDay> = ArrayList()
            val punchMiss: MutableList<CalendarDay> = ArrayList()

            //Weekend
            //Absent
            //Present
            //Holiday
            //Checkout Missing
            //Half Day

            for (i in calendarDays.indices) {
                when {
                    calendarDays[i].status.equals(TAG_ABSENT, ignoreCase = true) -> {
                        calAbsent.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    calendarDays[i].status.equals(TAG_PRESENT, ignoreCase = true) -> {
                        calPresent.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    calendarDays[i].status.equals(TAG_HALF_DAY, ignoreCase = true) -> {
                        halfDay.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    calendarDays[i].status.equals(TAG_HOLIDAY, ignoreCase = true) -> {
                        holiday.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    calendarDays[i].status.equals(TAG_WEEKEND, ignoreCase = true) -> {
                        //holiday.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    calendarDays[i].status.equals(TAG_CHECKOUT_MISSING, ignoreCase = true) -> {
                        punchMiss.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                    else -> {
                        holiday.add(CalendarDay.from(LocalDate.of(calendarDays[i].year, calendarDays[i].month, calendarDays[i].days)))
                    }
                }
            }


            // Since we are updating the UI, do the operation on the Main dispatcher
            withContext(Dispatchers.Main) {
                this@AttendanceFragment.widget?.addDecorator(EventDecorator(activity, calPresent, 0))
                this@AttendanceFragment.widget?.addDecorator(EventDecorator(activity,  calAbsent, 1))
                this@AttendanceFragment.widget?.addDecorator(EventDecorator(activity,  halfDay, 2))
                this@AttendanceFragment.widget?.addDecorator(EventDecorator(activity,  punchMiss, 3))
                this@AttendanceFragment.widget?.addDecorator(EventDecorator(activity,  holiday, 4))
            }
        }

    }

    override fun onValidationError(message: String, callFrom: String) {

    }
}
