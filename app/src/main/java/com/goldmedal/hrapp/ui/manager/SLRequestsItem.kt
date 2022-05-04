package com.goldmedal.hrapp.ui.manager

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.SLForApprovalData
import com.goldmedal.hrapp.databinding.SlRequestsItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class SLRequestsItem(private val data: SLForApprovalData?, private val callBackListener: OnApprovalClickListener?) : BindableItem<SlRequestsItemBinding>() {
    override fun bind(viewBinding: SlRequestsItemBinding, position: Int) {
        viewBinding.apply {
            txtEmployee.text = data?.EmployeeName ?: "-"
            txtAppliedOn.text = data?.AppliedOnDate ?: "-"
            textViewBranch.text = data?.BranchName ?: "-"
            textViewDepartment.text = data?.DepartmentName  ?: "-"
            txtDate.text = data?.LeaveDate  ?: "-"
            txtNoOfHours.text = (data?.NumberOfHrs ?: 0).toString()
            txtRemarks.text = data?.Remarks ?: "-"


            approveButton.setOnClickListener {
                callBackListener?.onApproval( data,type = 1)
            }

            disApproveButton.setOnClickListener {
                callBackListener?.onApproval( data,type = 2)
            }
        }
    }
    override fun getLayout() = R.layout.sl_requests_item
    override fun initializeViewBinding(view: View) = SlRequestsItemBinding.bind(view)



    interface OnApprovalClickListener{

        fun onApproval(item: SLForApprovalData?, type: Int)
    }
}