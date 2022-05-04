package com.goldmedal.hrapp.ui.manager

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.ODApprovalData
import com.goldmedal.hrapp.data.model.RegularizeAttendanceApprovalData
import com.goldmedal.hrapp.databinding.OdRequestsItemBinding
import com.goldmedal.hrapp.databinding.RegRequestsItemBinding
import com.goldmedal.hrapp.util.formatDateString
import com.xwray.groupie.viewbinding.BindableItem

class RegularizationRequestsItem(private val data: RegularizeAttendanceApprovalData?, private val callBackListener: OnApprovalClickListener?) : BindableItem<RegRequestsItemBinding>() {
    override fun bind(viewBinding: RegRequestsItemBinding, position: Int) {
        viewBinding.apply {
            txtEmployee.text = data?.EmployeeName
            txtPunchDate.text = data?.PunchDate
            txtAppliedOn.text = data?.AppliedOn
            textViewBranch.text = data?.BranchName
            textViewDepartment.text = data?.DepartmentName
            txtOldPunchIn.text = if(data?.OldPunchIn.equals("-")) "-" else formatDateString(data?.OldPunchIn ?: "", "HH:mm", "hh:mm a")
            txtOldPunchOut.text = if(data?.OldPunchOut.equals("-")) "-" else formatDateString(data?.OldPunchOut ?: "", "HH:mm", "hh:mm a")
            txtNewPunchIn.text = if(data?.NewPunchIn.equals("-")) "-" else formatDateString(data?.NewPunchIn ?: "", "HH:mm", "hh:mm a")
            txtNewPunchOut.text = if(data?.NewPunchOut.equals("-")) "-" else formatDateString(data?.NewPunchOut ?: "", "HH:mm", "hh:mm a")
            txtReason.text = data?.Reason
            txtRemarks.text = data?.Remark


            approveButton.setOnClickListener {
                callBackListener?.onApproval( data,type = 1)
            }

            disApproveButton.setOnClickListener {
                callBackListener?.onApproval( data,type = 2)
            }
        }
    }
    override fun getLayout() = R.layout.reg_requests_item
    override fun initializeViewBinding(view: View) = RegRequestsItemBinding.bind(view)



    interface OnApprovalClickListener{

        fun onApproval(item: RegularizeAttendanceApprovalData?, type: Int)
    }
}