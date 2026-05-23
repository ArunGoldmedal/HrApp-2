package com.goldmedal.hrapp.data.model.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.common.CornerImageView;
import com.goldmedal.hrapp.data.db.entities.HolidayData;
import com.zhpan.bannerview.BaseViewHolder;


public class NoDataHolidayHolder extends BaseViewHolder<HolidayData> {

    public NoDataHolidayHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
        CornerImageView imageView = findViewById(R.id.banner_image);
        imageView.setRoundCorner(roundCorner);

    }


    @Override
    public void bindData(HolidayData data, int position, int pageSize) {
        TextView txtInfo = findViewById(R.id.txtInfo);
        txtInfo.setText("No Upcoming Holidays");
    }
}