package com.goldmedal.hrapp.ui.accounts


import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.LimitPartyDetailData
import com.goldmedal.hrapp.databinding.ItemPartyDetailBinding
import com.xwray.groupie.viewbinding.BindableItem


class PartyDetailItem(
        private val partyDetail: LimitPartyDetailData?
) : BindableItem<ItemPartyDetailBinding>() {


    override fun bind(viewBinding: ItemPartyDetailBinding, position: Int) {
        viewBinding.partyDetail = partyDetail
    }

    override fun getLayout() = R.layout.item_party_detail
    override fun initializeViewBinding(view: View): ItemPartyDetailBinding = ItemPartyDetailBinding.bind(view)

}