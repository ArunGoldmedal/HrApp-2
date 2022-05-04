package com.goldmedal.hrapp.ui.leftdrawer

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.AttendanceRegularizeByUserData
import com.goldmedal.hrapp.databinding.TeamRegHistoryItemBinding
import com.goldmedal.hrapp.util.formatDateString
import com.xwray.groupie.viewbinding.BindableItem

class TeamRegularizationHistoryItem(private val data: AttendanceRegularizeByUserData?, private val context: Context) : BindableItem<TeamRegHistoryItemBinding>() {
    override fun bind(viewBinding: TeamRegHistoryItemBinding, position: Int) {
        viewBinding.apply {
            txtEmployee.text = data?.EmployeeName
            textViewDepartment.text = data?.DepartmentName
            textViewBranch.text = data?.BranchName
            txtAppliedOn.text = data?.AppliedOn
            tvStatus.text = data?.ApprovalStatus
            txtPunchDate.text = data?.PunchDate
            txtOldPunchIn.text = if(data?.OldPunchIn.equals("-")) "-" else formatDateString(data?.OldPunchIn ?: "", "HH:mm", "hh:mm a")
            txtOldPunchOut.text = if(data?.OldPunchOut.equals("-")) "-" else formatDateString(data?.OldPunchOut ?: "", "HH:mm", "hh:mm a")
            txtNewPunchIn.text = if(data?.NewPunchIn.equals("-")) "-" else formatDateString(data?.NewPunchIn ?: "", "HH:mm", "hh:mm a")
            txtNewPunchOut.text = if(data?.NewPunchOut.equals("-")) "-" else formatDateString(data?.NewPunchOut ?: "", "HH:mm", "hh:mm a")
            txtReason.text = data?.Reason
            txtRemarks.text = data?.Remark

            when {
                data?.ApprovalStatus?.equals("Dis-Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_red_background)
                }
                data?.ApprovalStatus?.equals("open", ignoreCase = true) == true -> {
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)

                }
                data?.ApprovalStatus?.contains("Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_green_background)
                }
                else -> {
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }
            }
        }
    }
    override fun getLayout() = R.layout.team_reg_history_item
    override fun initializeViewBinding(view: View) = TeamRegHistoryItemBinding.bind(view)
}