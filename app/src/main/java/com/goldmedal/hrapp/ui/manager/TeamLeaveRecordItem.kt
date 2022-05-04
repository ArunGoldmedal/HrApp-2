package com.goldmedal.hrapp.ui.manager

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.goldmedal.hrapp.databinding.TeamLeaveRecordItemBinding
import com.goldmedal.hrapp.util.formatNumber
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*

class TeamLeaveRecordItem(
        private val data: LeaveRecordData?,
        private val context: Context, private val chipColors: Array<IntArray>
) : BindableItem<TeamLeaveRecordItemBinding>() {


    override fun bind(viewBinding: TeamLeaveRecordItemBinding, position: Int) {
        val randomColorIndex: Int = Random().nextInt(chipColors.size - 0) + 0
        data?.ChipTextColor = chipColors[randomColorIndex][1]
        data?.ChipBackgroundColor = chipColors[randomColorIndex][0]


        viewBinding.apply {
            textViewLeaveType.text = data?.LeaveType ?: "-"
            imvLeaveType.circleBackgroundColor = Color.parseColor(data?.LeaveTypeColor ?: "#d32f2f")
            textViewDayType.text = data?.DayType ?: "-"
            textViewLeaveStatus.text = data?.LeaveStatus ?: "-"
            textViewAppliedDate.text = formatNumber(data?.LeaveDays.toString()) + " Day(s)"

            textViewLeaveDuration.text = data?.LeaveDate ?: "-"
            chipLeaveReason.text = data?.LeaveReason ?: ""

            if (data?.ChipTextColor != null) {
                chipLeaveReason.setTextColor(data?.ChipTextColor!!)
            }
            if (data?.ChipBackgroundColor != null) {
                chipLeaveReason.chipBackgroundColor = ColorStateList.valueOf(data?.ChipBackgroundColor!!)
            }
        }
    }

    override fun getLayout() = R.layout.team_leave_record_item
    override fun initializeViewBinding(view: View) = TeamLeaveRecordItemBinding.bind(view)
}