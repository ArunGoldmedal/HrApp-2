package com.goldmedal.hrapp.ui.dialogs

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.AttendanceHistoryData
import com.goldmedal.hrapp.databinding.AttendanceHistoryRowBinding
import com.goldmedal.hrapp.ui.attendancedetail.AttendanceReportActivity
import com.xwray.groupie.viewbinding.BindableItem


class AttendanceHistoryItem(private val attendanceHistory: AttendanceHistoryData?, private val context: AppCompatActivity?) : BindableItem<AttendanceHistoryRowBinding>() {
    override fun bind(viewBinding: AttendanceHistoryRowBinding, position: Int) {

        viewBinding.apply {


            val avatar = if (attendanceHistory?.GenderId == 1) R.drawable.male_avatar else R.drawable.female_avatar

            context?.let {
                Glide.with(it)
                        .load(attendanceHistory?.ProfilePhoto)
                        .fitCenter()
                        .placeholder(avatar)
                        .into(imvProfilePic)
            }



            textViewDesignation.text = attendanceHistory?.Designation ?: "-"
            textViewName.text = attendanceHistory?.Username ?: "-"

            if (attendanceHistory?.FirstIn.isNullOrEmpty()) {
                textViewInTime.text = "-"
            } else {
                textViewInTime.text = attendanceHistory?.FirstIn ?: "-"
            }




            if (!attendanceHistory?.FirstIn.equals(attendanceHistory?.LastOut)) {
                textViewOutTime.text = attendanceHistory?.LastOut ?: "-"
            } else {
                textViewOutTime.text = "-"
            }
            textViewWorkingHrs.text = attendanceHistory?.TotalHours ?: "-"


            itemView.setOnClickListener {
                context?.let {
                    val intent = Intent(it, AttendanceReportActivity::class.java)
                    intent.putExtra("item", attendanceHistory)
                    it.startActivity(intent)
                }
            }
        }
    }


    override fun getLayout(): Int = R.layout.attendance_history_row
    override fun initializeViewBinding(view: View): AttendanceHistoryRowBinding = AttendanceHistoryRowBinding.bind(view)


}