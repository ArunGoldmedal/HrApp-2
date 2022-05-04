package com.goldmedal.hrapp.data.model.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.common.CornerImageView;
import com.goldmedal.hrapp.data.db.entities.HolidayData;
import com.zhpan.bannerview.BaseViewHolder;


public class NoDataHolidayHolder extends BaseViewHolder<HolidayData> {

    public NoDataHolidayHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
        CornerImageView imageView = findView(R.id.banner_image);
        imageView.setRoundCorner(roundCorner);

    }


    @Override
    public void bindData(HolidayData data, int position, int pageSize) {


        AppCompatTextView txtInfo = findView(R.id.txtInfo);

        txtInfo.setText("No Upcoming Holidays");

    }
}