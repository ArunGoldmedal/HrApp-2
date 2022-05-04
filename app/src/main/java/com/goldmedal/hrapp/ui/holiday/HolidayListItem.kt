package com.goldmedal.hrapp.ui.holiday

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.AllHolidayData
import com.goldmedal.hrapp.databinding.HolidayListRowBinding
import com.xwray.groupie.viewbinding.BindableItem
import java.util.*


class HolidayListItem(
        private val holidayData: AllHolidayData?
) : BindableItem<HolidayListRowBinding>() {


    override fun bind(viewBinding: HolidayListRowBinding, position: Int) {
        viewBinding.holidayListData = holidayData

        //val holidayImage = arrayOf(R.drawable.republic_day, R.drawable.holi, R.drawable.labor, R.drawable.rakhi, R.drawable.independence_day, R.drawable.ganesh_chaturthi, R.drawable.gandhi_jayanti, R.drawable.dushera, R.drawable.diwali, R.drawable.bhai_dooj)

        viewBinding.tvHolidayName.text = holidayData?.HolidayName

        if (holidayData?.FromDateDay.equals(holidayData?.ToDateDay)) {
            viewBinding.tvHolidayDay.text = holidayData?.FromDateDay
        } else {
            viewBinding.tvHolidayDay.text = holidayData?.FromDateDay + " - " + holidayData?.ToDateDay
        }

        if (holidayData?.FromDateFormat.equals(holidayData?.ToDateFormat)) {
            viewBinding.tvHolidayDate.text = holidayData?.FromDateFormat
        } else {
            viewBinding.tvHolidayDate.text = holidayData?.FromDateFormat + " - " + holidayData?.ToDateFormat
        }

        viewBinding.tvHolidayDescr.text = holidayData?.Description

        val holidayName: String = holidayData?.HolidayName?.toLowerCase(Locale.getDefault()) ?: ""

        //REPUBLIC DAY
        if (holidayName.contains("republic")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.republic_day)
        } else if (holidayName.contains("holi")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.holi)
        } else if (holidayName.contains("labor") || holidayName.contains("labour")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.labor)
        } else if (holidayName.contains("rakshabandhan") || holidayName.contains("rakhi")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.rakhi)
        } else if (holidayName.contains("independence")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.independence_day)
        } else if (holidayName.contains("ganesh")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.ganesh_chaturthi)
        } else if (holidayName.contains("gandhi")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.gandhi_jayanti)
        } else if (holidayName.contains("dush") || holidayName.contains("duss")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.dushera)
        } else if (holidayName.contains("diwali") || holidayName.contains("deep") || holidayName.contains("divali") || holidayName.contains("dipa")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.diwali)
        } else if (holidayName.contains("bhai dooj")) {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.bhai_dooj)
        } else {
            viewBinding.imvHolidayImage.setImageResource(R.drawable.holiday_default)
        }

    }

    override fun getLayout() = R.layout.holiday_list_row
    override fun initializeViewBinding(view: View): HolidayListRowBinding = HolidayListRowBinding.bind(view)

}