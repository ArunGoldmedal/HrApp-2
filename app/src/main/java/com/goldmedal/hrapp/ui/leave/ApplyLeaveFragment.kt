package com.goldmedal.hrapp.ui.leave

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ImageSelectionListener
import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData
import com.goldmedal.hrapp.data.model.AppliedLeaveCountData
import com.goldmedal.hrapp.data.model.LeaveApplyData
import com.goldmedal.hrapp.data.model.LeaveReasonsData
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.goldmedal.hrapp.data.network.GlobalConstant.IMAGE_DIRECTORY
import com.goldmedal.hrapp.databinding.ApplyLeaveDetailBinding
import com.goldmedal.hrapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.apply_leave_detail.*
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class ApplyLeaveFragment : Fragment(), ApiStageListener<Any>,  View.OnClickListener, ImageSelectionListener {




    private val applyLeaveModel: LeaveViewModel by viewModels()

    private lateinit var applyLeaveBinding: ApplyLeaveDetailBinding
    private lateinit var minEndDate: Calendar
    private lateinit var maxStartDate: Calendar


      private var dayTypeSegmentIndex: Int = 0


    private var totalLeavesCount: String? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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



        applyLeaveModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {

                applyLeaveModel.userId = user.UserID

                applyLeaveModel.leaveTypeData()
                applyLeaveModel.leaveReasons()
                applyLeaveModel.leaveBalanceApi(getCurrentFiscalYear())
            }
        })
        //Disable touch events
        slider.setOnTouchListener { v, event -> true }
        maxStartDate = Calendar.getInstance()
        minEndDate = Calendar.getInstance()

        rlSelectEndDate.setOnClickListener(this)
        rlSelectStartDate.setOnClickListener(this)
        rlSelectLeaveType.setOnClickListener(this)


           segmented_day_type {
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

        progress_bar?.start()


    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        if (callFrom == "leave_balance") {
            fillSlider(_object as List<LeaveBalanceData>)
        }


        if (callFrom == "leaveReasons") {
            populateLeaveReasonsSpinner(_object as List<LeaveReasonsData>)
        }
        if (callFrom == "appliedLeavesCount") {
            val leaveCountData = _object as List<AppliedLeaveCountData>

            applyLeaveModel.strActualLeaveDays = leaveCountData[0].ActualLeaveDays
            applyLeaveModel.strAppliedLeaveDays = leaveCountData[0].AppliedLeaveDays

            tvDuration?.text = applyLeaveModel.strActualLeaveDays
        }
        if (callFrom == "leaveType") {
            bindUI(_object as List<LeaveTypeData?>)
        }

        if (callFrom == "applyLeave") {


            val status = _object as List<LeaveApplyData>

            root_layout?.snackbar(status[0].Status ?: "Leave Applied Successfully")
            clearAllFields()

            //Refresh Leave Count at success...
            applyLeaveModel.leaveTypeData()
            applyLeaveModel.leaveBalanceApi(getCurrentFiscalYear())
        }
//        }
    }

    private fun bindUI(list: List<LeaveTypeData?>) {
        totalLeavesCount = list[0]?.LeaveCount
        tvSelectLeaveType?.text = list[0]?.LeaveTypeName
        applyLeaveModel.strLeaveTypeId = list[0]?.LeaveTypeID
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        root_layout?.snackbar(message)



        if (callFrom == "leave_balance") {
            applyLeaveModel.getLeaveBalanceData().observe(this, {
                if (it != null) {
                    fillSlider(it)
                }
            })
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id
        val mYear: Int
        val mMonth: Int
        val mDay: Int
        when (id) {
            R.id.rlSelectStartDate -> {


                // Get Current Date
//                val c = Calendar.getInstance()
                mYear = minEndDate[Calendar.YEAR]
                mMonth = minEndDate[Calendar.MONTH]
                mDay = minEndDate[Calendar.DAY_OF_MONTH]

                val previousCalendar = Calendar.getInstance()
                val minDay = getMinDateToApplyLeaves(mYear, mMonth + 1, mDay)
                previousCalendar.add(Calendar.DAY_OF_MONTH, -minDay)


                val startDatePicker = DatePickerDialog(requireContext(),
                        { view, year, monthOfYear, dayOfMonth ->
                            tvSelectStartDate.text = String.format(Locale.getDefault(), "%s/%d/%d", dayOfMonth.toString(), monthOfYear + 1, year)
                            minEndDate.set(year, monthOfYear, dayOfMonth)
                            applyLeaveModel.strStartDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year

                            if (applyLeaveModel.strEndDate.isNullOrEmpty()) {
                                root_layout?.snackbar("Please Select End Date")
                            } else {
                                applyLeaveModel.appliedLeavesCount()
                            }

                        }, mYear, mMonth, mDay)
                startDatePicker.datePicker.minDate = previousCalendar.timeInMillis

                if (dayTypeSegmentIndex > 0) {
                    if (applyLeaveModel.strEndDate?.isNotEmpty() == true) {
                        startDatePicker.datePicker.minDate = maxStartDate.timeInMillis
                        startDatePicker.datePicker.maxDate = maxStartDate.timeInMillis
                    }
                } else {
                    if (applyLeaveModel.strEndDate?.isNotEmpty() == true) {
                        startDatePicker.datePicker.maxDate = maxStartDate.timeInMillis
                    }
                }



                startDatePicker.show()

            }
            R.id.rlSelectEndDate -> {
                mYear = maxStartDate[Calendar.YEAR]
                mMonth = maxStartDate[Calendar.MONTH]
                mDay = maxStartDate[Calendar.DAY_OF_MONTH]

                val previousCalendar = Calendar.getInstance()
                val minDay = getMinDateToApplyLeaves(mYear, mMonth + 1, mDay)
                previousCalendar.add(Calendar.DAY_OF_MONTH, -minDay)

                val endDatePicker = DatePickerDialog(requireContext(),
                        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            tvSelectEndDate.text = String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, monthOfYear + 1, year)
                            maxStartDate.set(year, monthOfYear, dayOfMonth)
                            applyLeaveModel.strEndDate = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
                            if (applyLeaveModel.strStartDate.isNullOrEmpty()) {
                                root_layout?.snackbar("Please Select Start Date")
                            } else {
                                applyLeaveModel.appliedLeavesCount()
                            }
                        }, mYear, mMonth, mDay)


                if (dayTypeSegmentIndex > 0) {
                    if (applyLeaveModel.strStartDate?.isNotEmpty() == true) {
                        endDatePicker.datePicker.minDate = minEndDate.timeInMillis
                        endDatePicker.datePicker.maxDate = minEndDate.timeInMillis
                    }
                } else {
                    if (applyLeaveModel.strStartDate?.isNotEmpty() == true) {
                        endDatePicker.datePicker.minDate = minEndDate.timeInMillis
                    } else {
                        endDatePicker.datePicker.minDate = previousCalendar.timeInMillis
                    }
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
                    imgUpload!!.setImageBitmap(displayedBitmap)


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {


                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imgUpload!!.setImageBitmap(thumbnail)
                saveImage(context, IMAGE_DIRECTORY, thumbnail)
                Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                val scaledBitmap = scaleDown(thumbnail, 675f, true)

                applyLeaveModel.strBase64Image = convertBitmapToBase64(scaledBitmap)
            }
        }else if (requestCode == LAUNCH_LEAVE_TYPE) {
            if (resultCode == RESULT_OK) {

                val leaveType: LeaveTypeData? = data?.getParcelableExtra(LeaveTypeActivity.ARG_LEAVE_TYPE)

                applyLeaveModel.strLeaveTypeId = leaveType?.LeaveTypeID
                tvSelectLeaveType?.text = leaveType?.LeaveTypeName
            }
        }


    }

    override fun choosePhotoFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    override fun takePhotoFromCamera() {


        askPermission(Manifest.permission.CAMERA) {
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
        }


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
        spinnerLeaveReasons?.adapter = leaveReasonsAdapter



        spinnerLeaveReasons?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

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

            slider?.valueFrom = 0f

            if (totalLeaves > 0) {
                slider?.valueTo = totalLeaves.toFloat()
            }

            if (leaveBalance > 0) {
                slider?.value = leaveBalance.toFloat()
            }



            
            txt_rem_leaves?.text = formatNumber(leaveBalance.toString()) + " Leave Rem."
            txt_total_leaves?.text = formatNumber(totalLeaves.toString()) + " Leaves"

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
        segmented_day_type?.initialCheckedIndex = 0
        segmented_day_type?.setInitialCheckedItem()

        tvDuration?.text = "-"
        spinnerLeaveReasons?.setSelection(0)
        imgUpload?.setImageResource(R.drawable.image_upload)
        tvSelectStartDate?.text = "Select"
        tvSelectEndDate?.text = "Select"
        tvSelectLeaveType?.text = "Select"


    }


    private fun resetDates() {


        applyLeaveModel.strStartDate = ""
        applyLeaveModel.strEndDate = ""

        maxStartDate = Calendar.getInstance()
        minEndDate = Calendar.getInstance()

        applyLeaveModel.strAppliedLeaveDays = "0"
        applyLeaveModel.strActualLeaveDays = "0"


        tvDuration?.text = "-"
        tvSelectStartDate?.text = "Select"
        tvSelectEndDate?.text = "Select"


    }

    override fun onValidationError(message: String, callFrom: String) {
        root_layout?.snackbar(message)
    }

    companion object {
        internal const val LAUNCH_LEAVE_TYPE = 99
    }
}