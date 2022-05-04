package com.goldmedal.hrapp.ui.leave

import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.data.repositories.ILeaveListener
import com.goldmedal.hrapp.databinding.LeaveRequestsItemBinding
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*


class LeaveRequestsItem(private val leaveRequests: LeaveRequestsData?, private val chipColors: Array<IntArray>, private val context: AppCompatActivity?,private val listener: ILeaveListener?) : BindableItem<LeaveRequestsItemBinding>() {


    override fun bind(viewBinding: LeaveRequestsItemBinding, position: Int) {
        viewBinding.apply {

            val randomColorIndex: Int = Random().nextInt(chipColors.size - 0) + 0


            val avatar = if (leaveRequests?.GenderID == 1) R.drawable.male_avatar else R.drawable.female_avatar

            if (context != null) {
                Glide.with(context)
                        .load(leaveRequests?.ProfilePicture)
                        .fitCenter()
                        .placeholder(avatar)
                        .into(icon)
            }

            leaveRequests?.ChipTextColor = chipColors[randomColorIndex][1]
            leaveRequests?.ChipBackgroundColor = chipColors[randomColorIndex][0]

            textViewApplicantName.text = leaveRequests?.EmployeeName ?: "-"
            textViewAppliedDate.text = leaveRequests?.LeaveAppliedOn ?: "-"
            textViewLeaveDuration.text = leaveRequests?.LeaveDuration ?: "-"
            chipLeaveReason.text = leaveRequests?.LeaveReason ?: ""




            if (leaveRequests?.ReportingPersonGenderId?.isNotEmpty() ?: false) {
               reportingPersonLayout.visibility = View.VISIBLE

                val reportingPersonAvatar = if (leaveRequests?.ReportingPersonGenderId.equals("1")) R.drawable.male_avatar else R.drawable.female_avatar
                if (context != null) {
                    Glide.with(context)
                            .load(leaveRequests?.ReportingPersonProfilePic)
                            .fitCenter()
                            .placeholder(reportingPersonAvatar)
                            .into(imageViewReportingPerson)
                }


                textViewApprovalStatus.text = leaveRequests?.Status + " by"
                textViewReportingPerson.text = leaveRequests?.ReportingPersonName
                textViewDesignation.text = leaveRequests?.ReportingPersonDesignation


                if (leaveRequests?.Status.equals("Pending", ignoreCase = true)) {
                    if (context != null) {
                        textViewApprovalStatus.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
                    }

                }else{
                    if (context != null) {
                        textViewApprovalStatus.setTextColor(context.resources.getColor(R.color.colorGreen))
                    }
                }

            }else{
                reportingPersonLayout.visibility = View.GONE
            }

            chipLeaveReason.setTextColor(leaveRequests?.ChipTextColor!!)
            chipLeaveReason.chipBackgroundColor = ColorStateList.valueOf(leaveRequests?.ChipBackgroundColor!!)
            itemView.setOnClickListener {
                if (context != null) {
                val modalBottomSheet = RespondRequestsBottomSheet.newInstance(leaveRequests,position,listener)
                    modalBottomSheet.show(context.supportFragmentManager, RespondRequestsBottomSheet.TAG)
                }
            }
        }

    }
    override fun getLayout(): Int = R.layout.leave_requests_item
    override fun initializeViewBinding(view: View): LeaveRequestsItemBinding = LeaveRequestsItemBinding.bind(view)
}

