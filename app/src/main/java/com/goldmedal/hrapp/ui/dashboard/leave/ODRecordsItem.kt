package com.goldmedal.hrapp.ui.dashboard.leave

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.ODListData
import com.goldmedal.hrapp.databinding.OdRecordsItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class ODRecordsItem(private val data: ODListData?,private val context: Context) : BindableItem<OdRecordsItemBinding>() {
    override fun bind(viewBinding: OdRecordsItemBinding, position: Int) {
        viewBinding.apply {

            when {
                data?.ODStatus?.equals("Dis-Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_red_background)
                }
                data?.ODStatus?.equals("open", ignoreCase = true) == true -> {
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)

                }
                data?.ODStatus?.contains("Approved", ignoreCase = true) == true -> {

                    statusBackground.background = context.getDrawable(R.drawable.rounded_green_background)
                }
                else -> {
                    statusBackground.background = context.getDrawable(R.drawable.rounded_yellow_background)
                }
            }


            tvODStatus.text = data?.ODStatus
            tvAppliedDate.text = data?.AppliedDate ?: "-"
            tvDuration.text = "${data?.FromDate} - ${data?.ToDate}"
            tvOutSidePlace.text = data?.SubLocationName
        }
    }
    override fun getLayout() = R.layout.od_records_item
    override fun initializeViewBinding(view: View) = OdRecordsItemBinding.bind(view)
}