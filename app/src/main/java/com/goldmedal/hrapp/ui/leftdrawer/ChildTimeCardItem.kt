package com.goldmedal.hrapp.ui.leftdrawer

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.AttendanceDetailsData
import com.goldmedal.hrapp.databinding.ChildTimeCardRowBinding
import com.goldmedal.hrapp.util.AdapterCallbackListener
import com.xwray.groupie.viewbinding.BindableItem

class ChildTimeCardItem(
        private val data: AttendanceDetailsData?, private val callbackListener: AdapterCallbackListener?,
        private val context: Context?
) : BindableItem<ChildTimeCardRowBinding>() {
    override fun bind(viewBinding: ChildTimeCardRowBinding, position: Int) {

        viewBinding.apply {

            textViewInTime.text = data?.FirstIn
            textViewOutTime.text = data?.LastOut
            textViewWorkingHrs.text = data?.TotalHours

            buttonEdit.setOnClickListener {

                callbackListener?.onCallback(data?.DisplayDate,data?.FirstIn,data?.LastOut)

            }
        }

    }

    override fun getLayout(): Int  = R.layout.child_time_card_row

    override fun initializeViewBinding(view: View) = ChildTimeCardRowBinding.bind(view)
}