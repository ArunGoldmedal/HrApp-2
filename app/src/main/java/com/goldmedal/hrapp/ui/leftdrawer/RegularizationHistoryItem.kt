package com.goldmedal.hrapp.ui.leftdrawer

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.AttendanceRegularizationListData
import com.goldmedal.hrapp.data.model.LeaveRecordData

import com.goldmedal.hrapp.databinding.OdRecordsItemBinding
import com.goldmedal.hrapp.databinding.RegHistoryItemBinding
import com.goldmedal.hrapp.ui.leave.LeaveRecordItem
import com.goldmedal.hrapp.util.formatDateString
import com.xwray.groupie.viewbinding.BindableItem

class RegularizationHistoryItem(private val data: AttendanceRegularizationListData?, private val context: Context,private val cancelListener: OnCancelClickedListener?) : BindableItem<RegHistoryItemBinding>() {
    override fun bind(viewBinding: RegHistoryItemBinding, position: Int) {
        viewBinding.apply {


            tvStatus.text = data?.ApprovalStatus
            txtAppliedOn.text = data?.AppliedOn
            txtPunchDate.text = data?.PunchDate
            txtOldPunchIn.text = if(data?.OldPunchIn.equals("-")) "-" else formatDateString(data?.OldPunchIn ?: "", "HH:mm", "hh:mm a")
            txtOldPunchOut.text =if(data?.OldPunchOut.equals("-")) "-" else formatDateString(data?.OldPunchOut ?: "", "HH:mm", "hh:mm a")
            txtNewPunchIn.text = if(data?.NewPunchIn.equals("-")) "-" else formatDateString(data?.NewPunchIn ?: "", "HH:mm", "hh:mm a")
            txtNewPunchOut.text = if(data?.NewPunchOut.equals("-")) "-" else formatDateString(data?.NewPunchOut ?: "", "HH:mm", "hh:mm a")
            txtReason.text = data?.Reason
            txtRemarks.text = data?.Remark

            when {
                data?.ApprovalStatus?.equals("Dis-Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_red_background)
                    cancelButton.visibility = View.GONE
                }
                data?.ApprovalStatus?.equals("open", ignoreCase = true) == true -> {
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                    cancelButton.visibility = View.VISIBLE

                }
                data?.ApprovalStatus?.contains("Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_green_background)
                    cancelButton.visibility = View.GONE
                }
                data?.ApprovalStatus?.contains("Cancel", ignoreCase = true) == true -> {
                    cancelButton.visibility = View.GONE
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }
                else -> {
                    cancelButton.visibility = View.VISIBLE
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }
            }

            cancelButton.setOnClickListener {
                cancelListener?.onCancelClicked(data?.ID)
            }
        }
    }

    interface OnCancelClickedListener {
        fun onCancelClicked(recordId: Int?)
    }
    override fun getLayout() = R.layout.reg_history_item
    override fun initializeViewBinding(view: View) = RegHistoryItemBinding.bind(view)
}