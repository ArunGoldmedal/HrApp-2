package com.goldmedal.hrapp.ui.leave

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ColorTemplate
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.databinding.RequestsArchiveItemBinding
import com.goldmedal.hrapp.util.formatNumber
import com.xwray.groupie.viewbinding.BindableItem


import java.util.*

class RequestsArchiveItem(
        private val leaveRequests: LeaveRequestsData?,
       private val context: Context
) : BindableItem<RequestsArchiveItemBinding>() {
    private val chipColors: Array<IntArray> = arrayOf(
            intArrayOf(ColorTemplate.rgb("#c5cae9"), ColorTemplate.rgb("#65499c")), //INDIGO
            intArrayOf(ColorTemplate.rgb("#b2ebf2"), ColorTemplate.rgb("#009faf")), //CYAN
            intArrayOf(ColorTemplate.rgb("#c8e6c9"), ColorTemplate.rgb("#519657")), //GREEN
            intArrayOf(ColorTemplate.rgb("#f8bbd0"), ColorTemplate.rgb("#ba2d65")), //PINK
            intArrayOf(ColorTemplate.rgb("#bbdefb"), ColorTemplate.rgb("#2286c3")), //BLUE
            intArrayOf(ColorTemplate.rgb("#ffecb3"), ColorTemplate.rgb("#c8a415")), //AMBER
            intArrayOf(ColorTemplate.rgb("#f0f4c3"), ColorTemplate.rgb("#a8b545")) //LIME
    )

    override fun bind(viewBinding: RequestsArchiveItemBinding, position: Int) {
        viewBinding.apply {
            val randomColorIndex: Int = Random().nextInt(chipColors.size - 0) + 0
            val avatar = if (leaveRequests?.GenderID == 1) R.drawable.male_avatar else R.drawable.female_avatar

            Glide.with(context)
                    .load(leaveRequests?.ProfilePicture)
                    .fitCenter()
                    .placeholder(avatar)
                    .into(icon)

            leaveRequests?.ChipTextColor = chipColors[randomColorIndex][1]
            leaveRequests?.ChipBackgroundColor = chipColors[randomColorIndex][0]

            textViewApplicantName.text = leaveRequests?.EmployeeName ?: "-"
            textViewAppliedDate.text = leaveRequests?.LeaveAppliedOn ?: "-"
            textViewLeaveDuration.text = leaveRequests?.LeaveDuration ?: "-"
            textViewLeaveInDays.text = "${formatNumber(leaveRequests?.ActualLeavesDay.toString())} Day(s)"
            chipLeaveReason.text = leaveRequests?.LeaveReason ?: ""


            chipLeaveReason.setTextColor(leaveRequests?.ChipTextColor!!)
            chipLeaveReason.chipBackgroundColor = ColorStateList.valueOf(leaveRequests?.ChipBackgroundColor!!)
        }

    }

    override fun getLayout() = R.layout.requests_archive_item
    override fun initializeViewBinding(view: View): RequestsArchiveItemBinding = RequestsArchiveItemBinding.bind(view)
}