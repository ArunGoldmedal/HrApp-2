package com.goldmedal.hrapp.ui.leave

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.data.repositories.ILeaveListener
import com.goldmedal.hrapp.databinding.RespondRequestsBottomSheetBinding
import com.goldmedal.hrapp.ui.dashboard.leave.FullscreenImageActivity
import com.goldmedal.hrapp.util.activityContext
import com.goldmedal.hrapp.util.formatNumber
import com.goldmedal.hrapp.util.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.respond_requests_bottom_sheet.*


@AndroidEntryPoint
class RespondRequestsBottomSheet : BottomSheetDialogFragment(),  ApiStageListener<Any> {



private val leaveModel: LeaveViewModel by viewModels()
    private lateinit var binding: RespondRequestsBottomSheetBinding
    private var item: LeaveRequestsData? = null

    private lateinit var dialog: BottomSheetDialog

    private lateinit var behavior: BottomSheetBehavior<View>

    private var segmentIndex: Int = 0
    private var leaveTypeSegmentIndex: Int = 0


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.respond_requests_bottom_sheet, container, false)
        return binding.root
    }


    companion object {
        const val TAG = "RespondRequestsBottomSheet"
        private var IListener: ILeaveListener? = null
        private var itemPosition: Int? = null
        fun newInstance(item: LeaveRequestsData?, position: Int, listener: ILeaveListener?): RespondRequestsBottomSheet {

            IListener = listener
            itemPosition = position

            val fragment = RespondRequestsBottomSheet()
            val args = Bundle()
            args.putSerializable("item", item)

            fragment.arguments = args
            return fragment
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            sheet.minimumHeight = 350


            val windowHeight = getWindowHeight()

            sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.peekHeight = windowHeight


        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.viewmodel = leaveModel
        leaveModel.apiListener = this


        leaveModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                leaveModel.userId = user.UserID

            }
        })

        item = arguments?.getSerializable("item") as LeaveRequestsData




        if (item?.LeaveImage.isNullOrEmpty()) {
            btnViewImage.visibility = View.GONE
        }


        val avatar = if (item?.GenderID == 1) R.drawable.male_avatar else R.drawable.female_avatar

        Glide.with((activityContext(requireContext()) as Activity))
                .load(item?.ProfilePicture)
                .fitCenter()
                .placeholder(avatar)
                .into(avatarIcon)




        leaveModel.applyLeaveId = item?.ApplyLeaveID
        leaveModel.employeeID = item?.EmployeeID
        leaveModel.strAppliedDays = item?.ActualLeavesDay.toString()

        edtComment.setHorizontallyScrolling(false)
        edtComment.maxLines = 4

        text_view_leave_duration.text = item?.LeaveDuration.toString()
        text_view_applicant_name.text = item?.EmployeeName
        text_view_applicant_designation.text = item?.EmployeeDesignation
        chip_leave_reason.text = item?.LeaveReason ?: "-"

        text_view_leave_type?.text = item?.LeaveType ?: "-"
        imvLeaveType?.circleBackgroundColor = Color.parseColor(item?.LeaveTypeColor ?: "#d32f2f")
        text_view_day_type?.text = item?.DayType ?: "-"


        text_view_applied_leaves.text = formatNumber(item?.ActualLeavesDay.toString())
        text_view_avail_leaves.text = formatNumber(item?.AvailableLeaves.toString())

        chip_leave_reason.setTextColor(item?.ChipTextColor ?: 0)
        chip_leave_reason.chipBackgroundColor = ColorStateList.valueOf(item?.ChipBackgroundColor
                ?: 0)


        segmented {

            // set initial checked segment (null by default)
            initialCheckedIndex = 0


            // notifies when segment was checked
            onSegmentChecked { segment ->

                if (segment.text.equals("Approve")) {

                    segmentIndex = 0
                } else {

                    segmentIndex = 1
                }
            }
        }

        segmented_leave_type {


            initialCheckedIndex = 0
            leaveModel.leaveType = leaveTypeSegmentIndex + 1

            onSegmentChecked { segment ->


                if (segment.text == "Paid") {
                    leaveTypeSegmentIndex = 0
                    leaveModel.leaveType = leaveTypeSegmentIndex + 1

                    leaveModel.strPaidLeave = "0"
                    leaveModel.strUnPaidLeave = "0"

                    partialPaidLayout?.visibility = View.GONE

                } else if (segment.text == "UnPaid") {
                    leaveTypeSegmentIndex = 1
                    leaveModel.leaveType = leaveTypeSegmentIndex + 1

                    leaveModel.strPaidLeave = "0"
                    leaveModel.strUnPaidLeave = "0"

                    partialPaidLayout?.visibility = View.GONE
                }
                //Partial Paid
                else {
                    leaveTypeSegmentIndex = 2
                    leaveModel.leaveType = leaveTypeSegmentIndex + 1
                    partialPaidLayout?.visibility = View.VISIBLE

                }
            }
        }




        textInputLayout.editText?.doOnTextChanged { inputText, start, before, count ->

            val inputNumber = inputText.toString().toDoubleOrNull()


            if (inputNumber != null) {

                if (inputNumber % 0.5 == 0.0) {
                    textInputLayout.error = null


                    getPartialLeaves(inputNumber)

                } else {
                    textInputLayout.error = "Input valid input"
                }
            } else {
                textInputLayout.error = "Input valid input"
                txtUnpaidLeaves.text = getString(R.string.str_unpaid_leave)
            }
        }


        imvClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        btnSend.setOnClickListener {


            if (segmentIndex == 0) {


                // In case of partial paid
                if (leaveTypeSegmentIndex == 2) {
                    if (edtPaidLeaves.text.toString().isEmpty()) {
                        context?.toast("Please enter paid leave")

                        return@setOnClickListener
                    } else if (!TextUtils.isEmpty(textInputLayout.error)) {
                        context?.toast("Please enter valid paid leave")

                        return@setOnClickListener
                    }
                }

                leaveModel.approveLeaves()
            } else {
                leaveModel.disApproveLeaves()
            }
        }

        btnViewImage?.setOnClickListener {
            FullscreenImageActivity.start(requireContext(),item?.LeaveImage)// "https://goldblobtest.blob.core.windows.net/goldappdata/goldapp/base/hrm/employeedocuments/profile_122-10628935-c973-4e78-a5ae-0ae238d490ec.jpg"
        }
    }

    private fun getPartialLeaves(inputNumber: Double) {
        val actualLeaves = item?.ActualLeavesDay ?: 0.0
        if (inputNumber <= actualLeaves) {



            val unpaidLeave = formatNumber(actualLeaves.minus(inputNumber).toString())

            leaveModel.strPaidLeave = inputNumber.toString()
            leaveModel.strUnPaidLeave = unpaidLeave

            txtUnpaidLeaves.text = getString(R.string.str_unpaid_leave) + " " + unpaidLeave
        } else {
            textInputLayout.error = "input valid input"
        }


    }


    private fun getWindowHeight(): Int {

        val displayMetrics = DisplayMetrics()
        (activityContext(context) as Activity?)?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return (displayMetrics.heightPixels.times(0.88)).toInt()
    }

    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        if (callFrom.equals("approveLeaves")) {
            context?.toast("Leave Approved Successfully!!!")

        } else if (callFrom.equals("disApproveLeaves")) {
            context?.toast("Leave Rejected Successfully!!!")

        }
        IListener?.observeRequests(itemPosition)
        dismiss()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        context?.toast(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }
}