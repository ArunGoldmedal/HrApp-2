package com.goldmedal.hrapp.ui.leftdrawer

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.GetAllAttendanceData
import com.goldmedal.hrapp.data.model.AttendanceDetailsData
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.goldmedal.hrapp.databinding.ExpandableTimeCardRowBinding
import com.goldmedal.hrapp.databinding.LeaveRecordRowBinding
import com.goldmedal.hrapp.ui.dashboard.attendance.AttendanceFragment
import com.goldmedal.hrapp.ui.leave.LeaveRecordItem
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import org.threeten.bp.LocalDate

class ExpandableTimeCardItem (
        private val data: AttendanceDetailsData?,
        private val context: Context
) : BindableItem<ExpandableTimeCardRowBinding>(),ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup
    override fun bind(viewBinding: ExpandableTimeCardRowBinding, position: Int) {
        viewBinding.apply {

            tvDate.text = data?.DisplayDate
            tvLeaveStatus.text = data?.status


            when {
                data?.status.equals(AttendanceFragment.TAG_ABSENT, ignoreCase = true) -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(R.color.colorRed))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_red_background)
                }
                data?.status.equals(AttendanceFragment.TAG_PRESENT, ignoreCase = true) -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(R.color.colorGreen))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_green_background)
                }
                data?.status.equals(AttendanceFragment.TAG_HOLIDAY, ignoreCase = true) -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(android.R.color.holo_purple))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_purple_background)
                }
                data?.status.equals(AttendanceFragment.TAG_WEEKEND, ignoreCase = true) -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(R.color.colorMissedPunch))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }
                data?.status.equals(AttendanceFragment.TAG_CHECKOUT_MISSING, ignoreCase = true) -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(R.color.colorOrange))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_orange_background)
                }
                else -> {
                    viewDivider.setBackgroundColor(context.resources.getColor(R.color.colorMissedPunch))
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }


            }

            itemView.setOnClickListener {
                expandableGroup.onToggleExpanded()
                changeStuff(viewBinding)
            }
        }

    }

    override fun getLayout() = R.layout.expandable_time_card_row

    override fun initializeViewBinding(view: View) = ExpandableTimeCardRowBinding.bind(view)

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }

    private fun changeStuff(viewBinding: ExpandableTimeCardRowBinding) {

        viewBinding.indicator.apply {
            setImageResource(
                    if (expandableGroup.isExpanded) R.drawable.ic_up
                    else R.drawable.ic_down)
        }

    }
}