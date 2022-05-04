package com.goldmedal.hrapp.ui.dashboard.leave

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.SLHistoryData
import com.goldmedal.hrapp.databinding.SlHistoryItemBinding
import com.xwray.groupie.viewbinding.BindableItem
 
class SLHistoryItem(private val data: SLHistoryData?) : BindableItem<SlHistoryItemBinding>() {
    override fun bind(viewBinding: SlHistoryItemBinding, position: Int) {
        viewBinding.apply {


            tvDate.text = data?.Date
            tvNoOfHrs.text = data?.NumberOfHrs.toString() ?: "-"
            tvRemarks.text = data?.Remark
            tvAppliedOn.text = data?.AppliedOn
            tvStatus.text = data?.Status
        }
    }
    override fun getLayout() = R.layout.sl_history_item
    override fun initializeViewBinding(view: View) = SlHistoryItemBinding.bind(view)
}