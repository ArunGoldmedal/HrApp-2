package com.goldmedal.hrapp.ui.dashboard.leave

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.SubLocationsData
import com.goldmedal.hrapp.databinding.SubLocationItemBinding
import com.goldmedal.hrapp.util.getAddressFromLatLong
import com.xwray.groupie.viewbinding.BindableItem

class SubLocationsItem(private val data: SubLocationsData?, private val callBackListener: OnListClickListener?) : BindableItem<SubLocationItemBinding>() {
    override fun bind(viewBinding: SubLocationItemBinding, position: Int) {
        viewBinding.apply {
            textViewName.text = data?.SublocationName
            textViewAddress.text = data?.SubLocationAddress ?: "Exact Location Unknown"

            itemView.setOnClickListener {
                callBackListener?.onItemClick( data)
            }
        }
    }
    override fun getLayout() = R.layout.sub_location_item
    override fun initializeViewBinding(view: View) = SubLocationItemBinding.bind(view)


}