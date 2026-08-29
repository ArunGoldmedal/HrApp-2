package com.goldmedal.hrapp.ui.leave

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData
import com.goldmedal.hrapp.data.model.AppliedLeaveCountData
import com.goldmedal.hrapp.data.model.LeaveApplyData
import com.goldmedal.hrapp.data.model.LeaveReasonsData
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.data.network.GlobalConstant.IMAGE_DIRECTORY
import com.goldmedal.hrapp.data.network.responses.BlockMonthDateData
import com.goldmedal.hrapp.databinding.ApplyLeaveDetailBinding
import com.goldmedal.hrapp.ui.dashboard.attendance.AttendanceViewModel
import com.goldmedal.hrapp.util.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class ApplyLeaveFragment : Fragment(), ApiStageListener<Any>, View.OnClickListener, ImageSelectionListener,
    EasyPermissions.PermissionCallbacks {
    private val applyLeaveModel: LeaveViewModel by viewModels()
    private val viewModel: AttendanceViewModel by activityViewModels()
    private lateinit var applyLeaveBinding: ApplyLeaveDetailBinding
    private lateinit var minEndDate: Calendar
    private lateinit var maxStartDate: Calendar
    private var dayTypeSegmentIndex: Int = 0
    private var totalLeavesCount: String? = null
    private lateinit var mBlockMonthDateData: BlockMonthDateData
    private var leaveCount = ""
    private var leaveId = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        applyLeaveBinding = DataBindingUtil.inflate(inflater, R.layout.apply_leave_detail, container, false)
        return applyLeaveBinding.rootLayout
    }

    private val GALLERY = 1
    private val CAMERA = 2

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        applyLeaveBinding.applyLeaveModel = applyLeaveModel
        applyLeaveModel.apiListener = this
        applyLeaveModel.imageSelectionListener = this

        // Use post{} so data binding executes first, then our listener overrides it
        applyLeaveBinding.btnSubmit.post {
            applyLeaveBinding.btnSubmit.setOnClickListener {
                onApplyLeaveClicked()
            }
        }

        applyLeaveModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {

                applyLeaveModel.userId = user.UserID

                applyLeaveModel.leaveTypeData()
                applyLeaveModel.leaveReasons()
                applyLeaveModel.getBlockMonthDate()
                applyLeaveModel.leaveBalanceApi(getCurrentFiscalYear())
            }
        })
        //Disable touch events
        applyLeaveBinding.slider.setOnTouchListener { v, event -> true }
        maxStartDate = Calendar.getInstance()
        minEndDate = Calendar.getInstance()

        applyLeaveBinding.rlSelectEndDate.setOnClickListener(this)
        applyLeaveBinding.rlSelectStartDate.setOnClickListener(this)
        applyLeaveBinding.rlSelectLeaveType.setOnClickListener(this)
        applyLeaveBinding.segmentedDayType {
            initialCheckedIndex = 0
            onSegmentChecked { segment ->

                resetDates()
                 applyLeaveModel.strDayType = segment.text as String?
                if (segment.text == "Full") {
                    dayTypeSegmentIndex = 0
                    applyLeaveModel.strLeaveConsiderId = 0

                } else if (segment.text == "First-Half") {
                    dayTypeSegmentIndex = 1
                    applyLeaveModel.strLeaveConsiderId = 1
                }
                //Second Half
                else {
                    dayTypeSegmentIndex = 2
                     applyLeaveModel.strLeaveConsiderId = 1
                }
            }
        }
    }


    override fun onStarted(callFrom: String) {

        applyLeaveBinding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        applyLeaveBinding.progressBar.stop()

        if (callFrom == "leave_balance") {
            fillSlider(_object as List<LeaveBalanceData>)
        }

        if (callFrom == "leaveReasons") {
            populateLeaveReasonsSpinner(_object as List<LeaveReasonsData>)
        }

        if (callFrom == GlobalConstant.BLOCK_MONTH_DATE_API) {
            val blockMonthDateList = _object as List<BlockMonthDateData>
            mBlockMonthDateData = blockMonthDateList[0]
            //Log.d("TAG", "Block Date: ${mBlockMonthDateData.monthblockdate}")
        }

        if (callFrom == "appliedLeavesCount") {
            val leaveCountData = _object as List<AppliedLeaveCountData>

            applyLeaveModel.strActualLeaveDays = leaveCountData[0].ActualLeaveDays
            applyLeaveModel.strAppliedLeaveDays = leaveCountData[0].AppliedLeaveDays

            applyLeaveBinding.tvDuration.text = applyLeaveModel.strActualLeaveDays
        }
        if (callFrom == "leaveType") {
            //bindUI(_object as List<LeaveTypeData?>)
            val leaveTypeData = _object as List<LeaveTypeData>
            leaveId = leaveTypeData[0].LeaveTypeID.toString()
            leaveCount = leaveTypeData[0].LeaveTypeName?.substringAfter("~", "")?.trim().toString()
            bindUI(leaveTypeData)
        }

        if (callFrom == "applyLeave") {

            val status = _object as List<LeaveApplyData>

            applyLeaveBinding.rootLayout.snackbar(status[0].Status ?: "Leave Applied Successfully")
            clearAllFields()

            //Refresh Leave Count at success...
            applyLeaveModel.leaveTypeData()
            applyLeaveModel.leaveBalanceApi(getCurrentFiscalYear())
        }
//        }
    }

    private fun bindUI(list: List<LeaveTypeData?>) {
        totalLeavesCount = list[0]?.LeaveCount
        applyLeaveBinding.tvSelectLeaveType.text = list[0]?.LeaveTypeName
        applyLeaveModel.strLeaveTypeId = list[0]?.LeaveTypeID
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        applyLeaveBinding.progressBar.stop()
        applyLeaveBinding.rootLayout.snackbar(message)

        if (callFrom == "leave_balance") {
            applyLeaveModel.getLeaveBalanceData().observe(this) {
                if (it != null) {
                    fillSlider(it)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        when (id) {
            R.id.rlSelectStartDate -> {

                mYear = minEndDate[Calendar.YEAR]
                mMonth = minEndDate[Calendar.MONTH]
                mDay = minEndDate[Calendar.DAY_OF_MONTH]

                val startDatePicker = DatePickerDialog(requireContext(),
                        { view, year, monthOfYear, dayOfMonth ->
                            applyLeaveBinding.tvSelectStartDate.text = String.format(Locale.getDefault(), "%s/%d/%d", dayOfMonth.toString(), monthOfYear + 1, year)
                            minEndDate.set(year, monthOfYear, dayOfMonth)
                            applyLeaveModel.strStartDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                            // Reset end date and duration whenever start date changes
                            applyLeaveModel.strEndDate = ""
                            applyLeaveModel.strActualLeaveDays = "0"
                            applyLeaveModel.strAppliedLeaveDays = "0"
                            applyLeaveBinding.tvDuration.text = "-"
                            applyLeaveBinding.tvSelectEndDate.text = "Select"
                            maxStartDate = Calendar.getInstance()

                            applyLeaveBinding.rootLayout.snackbar("Please Select End Date")

                        }, mYear, mMonth, mDay)

                //Log.d("monStartLeave", "monthStartDate: ${viewModel.monthStartDate}")
                //Log.d("monStartLeave", "monthEndDate: ${viewModel.monthEndDate}")

                // Set minDate from API (monthStartDate), fallback to today
                val startMinCalendar = if (!viewModel.monthStartDate.isNullOrEmpty()) {
                    getCalendarFromDateTimeString(viewModel.monthStartDate!!)
                } else {
                    Calendar.getInstance()
                }
                startDatePicker.datePicker.minDate = startMinCalendar.timeInMillis

                // Set maxDate from API (monthEndDate), fallback to no max
                if (!viewModel.monthEndDate.isNullOrEmpty()) {
                    startDatePicker.datePicker.maxDate = getCalendarFromDateTimeString(viewModel.monthEndDate!!).timeInMillis
                }

                startDatePicker.show()

            }
            R.id.rlSelectEndDate -> {
                mYear = maxStartDate[Calendar.YEAR]
                mMonth = maxStartDate[Calendar.MONTH]
                mDay = maxStartDate[Calendar.DAY_OF_MONTH]

                val endDatePicker = DatePickerDialog(requireContext(),
                        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            applyLeaveBinding.tvSelectEndDate.text = String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, monthOfYear + 1, year)
                            maxStartDate.set(year, monthOfYear, dayOfMonth)
                            applyLeaveModel.strEndDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                            if (applyLeaveModel.strStartDate.isNullOrEmpty()) {
                                applyLeaveBinding.tvDuration.text = "-"
                                applyLeaveBinding.rootLayout.snackbar("Please Select Start Date")
                            } else {
                                applyLeaveModel.appliedLeavesCount()
                            }
                        }, mYear, mMonth, mDay)

                // Set minDate: start date if selected, else monthStartDate, else today
                if (applyLeaveModel.strStartDate?.isNotEmpty() == true) {
                    endDatePicker.datePicker.minDate = minEndDate.timeInMillis
                } else if (!viewModel.monthStartDate.isNullOrEmpty()) {
                    endDatePicker.datePicker.minDate = getCalendarFromDateTimeString(viewModel.monthStartDate!!).timeInMillis
                }

                // Set maxDate from API (monthEndDate)
                if (!viewModel.monthEndDate.isNullOrEmpty()) {
                    endDatePicker.datePicker.maxDate = getCalendarFromDateTimeString(viewModel.monthEndDate!!).timeInMillis
                }

                if (dayTypeSegmentIndex > 0 && applyLeaveModel.strStartDate?.isNotEmpty() == true) {
                    endDatePicker.datePicker.maxDate = minEndDate.timeInMillis
                }

                endDatePicker.show()
            }

            R.id.rlSelectLeaveType -> {
             startActivityForResult(Intent(requireContext(),LeaveTypeActivity::class.java),LAUNCH_LEAVE_TYPE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                    val scaledBitmap = scaleDown(bitmap, 675f, true)

                    applyLeaveModel.strBase64Image = convertBitmapToBase64(scaledBitmap)

                    saveImage(context, IMAGE_DIRECTORY, bitmap)
                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                    val displayedBitmap = scaleDown(bitmap, 150f, true)
                    applyLeaveBinding.imgUpload.setImageBitmap(displayedBitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {


                val thumbnail = data!!.extras!!.get("data") as Bitmap
                applyLeaveBinding.imgUpload.setImageBitmap(thumbnail)
                saveImage(context, IMAGE_DIRECTORY, thumbnail)
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                val scaledBitmap = scaleDown(thumbnail, 675f, true)

                applyLeaveModel.strBase64Image = convertBitmapToBase64(scaledBitmap)
            }
        }else if (requestCode == LAUNCH_LEAVE_TYPE) {
            if (resultCode == RESULT_OK) {

                val leaveType: LeaveTypeData? = data?.getParcelableExtra(LeaveTypeActivity.ARG_LEAVE_TYPE)

                applyLeaveModel.strLeaveTypeId = leaveType?.LeaveTypeID
                applyLeaveBinding.tvSelectLeaveType.text = leaveType?.LeaveTypeName
            }
        }
    }

    override fun choosePhotoFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    override fun takePhotoFromCamera() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                activity as Activity,
                getString(R.string.str_camera_permission),
                CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }

        /*askPermission(Manifest.permission.CAMERA) {
            //all permissions already granted or just granted

            // your action
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)

        }.onDeclined { e ->
            if (e.hasDenied()) {
                //  appendText(resultView, "Denied :")
                //the list of denied permissions
                e.denied.forEach {
                }

                context?.let {
                    AlertDialog.Builder(it)
                            .setMessage(getString(R.string.str_camera_permission))
                            .setPositiveButton("CONTINUE") { dialog, which ->
                                e.askAgain()
                            } //ask again
                            .setNegativeButton("NOT NOW") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                }
            }

            if (e.hasForeverDenied()) {
                // appendText(resultView, "ForeverDenied :")
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach {
                    //    appendText(resultView, it)
                }
                // you need to open setting manually if you really need it
                e.goToSettings()
            }
        }*/
    }


    private fun populateLeaveReasonsSpinner(leaveReasonsList: List<LeaveReasonsData>) {

        val listOfLeaveReasons: MutableList<LeaveReasonsData> = mutableListOf()
        listOfLeaveReasons.add(LeaveReasonsData("-1", "Please Select"))

        for (listContents in leaveReasonsList) {
            listOfLeaveReasons.add(listContents)
        }

        val leaveReasonsAdapter: ArrayAdapter<LeaveReasonsData>? = context?.let {
            ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, listOfLeaveReasons)

        };

        // Apply the adapter to the spinner
        applyLeaveBinding.spinnerLeaveReasons.adapter = leaveReasonsAdapter

        applyLeaveBinding.spinnerLeaveReasons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val leaveReasons = leaveReasonsAdapter?.getItem(position);

                applyLeaveModel.strLeaveReasonId = leaveReasons?.LeaveReasonID
            }
        }
    }

    private fun fillSlider(list: List<LeaveBalanceData>) {
        if (list.isNotEmpty()) {

            val totalLeaves = list[0].TotalLeaves ?: 0.0
            val leavesTaken = list[0].LeavesTaken ?: 0.0
            val leaveBalance = totalLeaves - leavesTaken

            applyLeaveBinding.slider.valueFrom = 0f

            if (totalLeaves > 0) {
                applyLeaveBinding.slider.valueTo = totalLeaves.toFloat()
            }

            if (leaveBalance > 0) {
                applyLeaveBinding.slider.value = leaveBalance.toFloat()
            }

            applyLeaveBinding.txtRemLeaves.text = formatNumber(leaveBalance.toString()) + " Leave Rem."
            applyLeaveBinding.txtTotalLeaves.text = formatNumber(totalLeaves.toString()) + " Leaves"

        }
    }

    //Reset everything on Api success
    private fun clearAllFields() {

        applyLeaveModel.strStartDate = ""
        applyLeaveModel.strEndDate = ""
        maxStartDate = Calendar.getInstance()
        minEndDate = Calendar.getInstance()

        applyLeaveModel.strBase64Image = ""
        applyLeaveModel.strLeaveReasonId = "-1"
        applyLeaveModel.strAppliedLeaveDays = "0"
        applyLeaveModel.strActualLeaveDays = "0"
        applyLeaveModel.strLeaveConsiderId = 0
        applyLeaveModel.strDayType = "Full"

        //Re-select Full Segment Button
        applyLeaveBinding.segmentedDayType.initialCheckedIndex = 0
        applyLeaveBinding.segmentedDayType.setInitialCheckedItem()

        applyLeaveBinding.tvDuration.text = "-"
        applyLeaveBinding.spinnerLeaveReasons.setSelection(0)
        applyLeaveBinding.imgUpload.setImageResource(R.drawable.image_upload)
        applyLeaveBinding.tvSelectStartDate.text = "Select"
        applyLeaveBinding.tvSelectEndDate.text = "Select"
        applyLeaveBinding.tvSelectLeaveType.text = "Select"

    }


    private fun resetDates() {


        applyLeaveModel.strStartDate = ""
        applyLeaveModel.strEndDate = ""

        maxStartDate = Calendar.getInstance()
        minEndDate = Calendar.getInstance()

        applyLeaveModel.strAppliedLeaveDays = "0"
        applyLeaveModel.strActualLeaveDays = "0"


        applyLeaveBinding.tvDuration.text = "-"
        applyLeaveBinding.tvSelectStartDate.text = "Select"
        applyLeaveBinding.tvSelectEndDate.text = "Select"


    }

    override fun onValidationError(message: String, callFrom: String) {
        applyLeaveBinding.rootLayout.snackbar(message)
    }

    fun onApplyLeaveClicked() {
        // If leave reason not yet selected, let ViewModel handle all validations in order
        // (start date → end date → leave reason → duration)
        if (applyLeaveModel.strLeaveReasonId.isNullOrEmpty() || applyLeaveModel.strLeaveReasonId == "-1") {
            applyLeaveModel.onApplyLeavesButtonClick(applyLeaveBinding.root)
            return
        }

        // Leave reason is selected — check leave balance only for leaveId == "3"
        val duration = applyLeaveModel.strActualLeaveDays?.toDoubleOrNull() ?: 0.0
        val totalApplied = applyLeaveModel.strAppliedLeaveDays?.toDoubleOrNull() ?: 0.0
        val availableLeaves = leaveCount.toDoubleOrNull() ?: 0.0

        if (leaveId == "3" && leaveCount.isNotEmpty() && duration > availableLeaves) {
            AlertDialog.Builder(requireContext())
                .setTitle("Limit Exceeded")
                .setMessage("You can apply a maximum of $availableLeaves days for this leave type. Please adjust your selected dates.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }
        // All other validations are handled in ViewModel
        applyLeaveModel.onApplyLeavesButtonClick(applyLeaveBinding.root)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            context?.let { SettingsDialog.Builder(it).build().show() }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    companion object {
        internal const val LAUNCH_LEAVE_TYPE = 99
        const val CAMERA_PERM = 121
    }
}