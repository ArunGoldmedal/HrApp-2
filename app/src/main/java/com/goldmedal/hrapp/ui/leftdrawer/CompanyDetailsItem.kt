package com.goldmedal.hrapp.ui.leftdrawer

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.GetCompanyDetailsData
import com.goldmedal.hrapp.data.model.SubLocationsData
import com.goldmedal.hrapp.databinding.RowCompanyDetailsBinding
import com.goldmedal.hrapp.ui.dashboard.leave.OnListClickListener
import com.xwray.groupie.viewbinding.BindableItem

class CompanyDetailsItem(
    private val companyDetailData: GetCompanyDetailsData,
    private val callBackListener: OnItemClickListener?
): BindableItem<RowCompanyDetailsBinding>() {

    override fun bind(viewBinding: RowCompanyDetailsBinding, position: Int) {
        viewBinding.tvCompanyName.text = companyDetailData.CompanyName
        viewBinding.tvDate.text = companyDetailData.CreateDate
        viewBinding.tvCompanyAddress.text = companyDetailData.CompanyAddress

        viewBinding.btnViewVisitingCard.setOnClickListener {
            callBackListener?.onItemClick(companyDetailData, true)
        }
        viewBinding.btnViewProductImage.setOnClickListener {
            callBackListener?.onItemClick(companyDetailData, false)
        }
        viewBinding.btnDelete.setOnClickListener {
            callBackListener?.onDelete(companyDetailData, position)
        }
    }

    override fun getLayout() = R.layout.row_company_details

    override fun initializeViewBinding(view: View): RowCompanyDetailsBinding = RowCompanyDetailsBinding.bind(view)

    interface OnItemClickListener {
        fun onItemClick(model: GetCompanyDetailsData?, isVisitingCard: Boolean)
        fun onDelete(model: GetCompanyDetailsData?, position: Int)
    }
}