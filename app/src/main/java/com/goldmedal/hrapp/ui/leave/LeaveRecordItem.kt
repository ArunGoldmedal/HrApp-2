package com.goldmedal.hrapp.ui.leave


import android.content.Context
import android.graphics.Color
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.goldmedal.hrapp.databinding.LeaveRecordRowBinding
import com.goldmedal.hrapp.util.formatNumber
import com.xwray.groupie.viewbinding.BindableItem


class LeaveRecordItem(
        private val leaveRecord: LeaveRecordData?, private val cancelListener: OnCancelClickedListener?,
        private val context: Context
) : BindableItem<LeaveRecordRowBinding>() {


    interface OnCancelClickedListener {
        fun onCancelClicked(item: LeaveRecordData?)
    }

    override fun bind(viewBinding: LeaveRecordRowBinding, position: Int) {
        viewBinding.apply {

            tvLeaveStatus.text = leaveRecord?.LeaveStatus ?: "-"
            tvLeaveReason.text = leaveRecord?.LeaveReason ?: "-"
            tvLeaveDate.text = leaveRecord?.LeaveDate ?: "-"
            tvLeaveDays.text = formatNumber(leaveRecord?.LeaveDays.toString()) + " Day(s)"
            tvPaidLeaves.text = formatNumber(leaveRecord?.PaidLeave.toString()) + " Day(s)"
            tvUnPaidLeaves.text = formatNumber(leaveRecord?.UnPaidLeave.toString()) + " Day(s)"
            tvAppliedDate.text = leaveRecord?.AppliedDate ?: "-"
            tvLeaveType.text = leaveRecord?.LeaveType ?: "-"
            imvLeaveType.circleBackgroundColor = Color.parseColor(leaveRecord?.LeaveTypeColor ?: "#d32f2f")
            tvDayType.text = leaveRecord?.DayType ?: "-"


            if (leaveRecord?.LeaveStatus?.equals("Pending", ignoreCase = true) == true) {
                statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                cancelButton.visibility = View.VISIBLE
            }
            else if (leaveRecord?.LeaveStatus?.equals("Approved", ignoreCase = true) == true) {
                cancelButton.visibility = View.GONE
                statusBackground.background = context.getDrawable(R.drawable.rounded_green_background)
            } else if (leaveRecord?.LeaveStatus?.equals("Dis-Approved", ignoreCase = true) == true) {
                cancelButton.visibility = View.GONE
                statusBackground.background = context.getDrawable(R.drawable.rounded_red_background)
            } else if (leaveRecord?.LeaveStatus?.equals("Cancelled", ignoreCase = true) == true) {
                cancelButton.visibility = View.GONE
                statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
            }
            else if (leaveRecord?.LeaveStatus?.equals("Partially Approved", ignoreCase = true) == true) {
                cancelButton.visibility = View.GONE
                statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
            }
            else {
                cancelButton.visibility = View.VISIBLE
                statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
            }
            cancelButton.setOnClickListener {


                cancelListener?.onCancelClicked(leaveRecord)


            }


        }
    }

    override fun getLayout() = R.layout.leave_record_row
    override fun initializeViewBinding(view: View): LeaveRecordRowBinding = LeaveRecordRowBinding.bind(view)

}