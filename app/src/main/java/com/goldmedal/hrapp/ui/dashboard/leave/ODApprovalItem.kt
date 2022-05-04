package com.goldmedal.hrapp.ui.dashboard.leave

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.ODApprovalData
import com.goldmedal.hrapp.databinding.OdRequestsItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class ODApprovalItem(private val data: ODApprovalData?, private val callBackListener: OnApprovalClickListener?) : BindableItem<OdRequestsItemBinding>() {
    override fun bind(viewBinding: OdRequestsItemBinding, position: Int) {
        viewBinding.apply {
            textViewEmployee.text = data?.EmployeeName
            textViewDepartment.text = data?.DepartmentName
            textViewBranch.text = data?.BranchName
            tvAppliedDate.text = data?.AppliedDate
            tvDuration.text = "${data?.FromDate} - ${data?.ToDate}"
            tvOutSidePlace.text = data?.Sublocation
            tvRemarks.text = data?.Remarks


            imageViewAccept.setOnClickListener {
                callBackListener?.onApproval( data,type = 1)
            }

            imageViewReject.setOnClickListener {
                callBackListener?.onApproval( data,type = 2)
            }
        }
    }
    override fun getLayout() = R.layout.od_requests_item
    override fun initializeViewBinding(view: View) = OdRequestsItemBinding.bind(view)



    interface OnApprovalClickListener{

        fun onApproval(item: ODApprovalData?,type: Int)
    }
}