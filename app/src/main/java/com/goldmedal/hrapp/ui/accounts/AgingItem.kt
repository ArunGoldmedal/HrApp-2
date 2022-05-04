package com.goldmedal.hrapp.ui.accounts

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.AgingDetail
import com.goldmedal.hrapp.databinding.ItemAgingBinding
import com.xwray.groupie.viewbinding.BindableItem


class AgingItem(
        private val agingDetail: AgingDetail?
) : BindableItem<ItemAgingBinding>() {


    override fun bind(viewBinding: ItemAgingBinding, position: Int) {
        viewBinding.agingDetail = agingDetail
    }

    override fun getLayout() = R.layout.item_aging
    override fun initializeViewBinding(view: View): ItemAgingBinding = ItemAgingBinding.bind(view)

}