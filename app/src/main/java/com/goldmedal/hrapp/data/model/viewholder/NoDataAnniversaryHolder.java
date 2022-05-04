package com.goldmedal.hrapp.data.model.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.common.CornerImageView;
import com.goldmedal.hrapp.data.db.entities.AnniversaryData;
import com.zhpan.bannerview.BaseViewHolder;


public class NoDataAnniversaryHolder extends BaseViewHolder<AnniversaryData> {

    public NoDataAnniversaryHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
        CornerImageView imageView = findView(R.id.banner_image);
        imageView.setRoundCorner(roundCorner);

    }

//    @Override
//    public void bindData(Integer data, int position, int pageSize) {
//        setImageResource(R.id.banner_image, data);
//    }

    @Override
    public void bindData(AnniversaryData data, int position, int pageSize) {
        //  CornerImageView imageView = findView(R.id.banner_image);
        // Glide.with(imageView).load(data.getDescription()).placeholder(R.drawable.gandhi_jayanti).into(imageView);

        AppCompatTextView txtInfo = findView(R.id.txtInfo);

        txtInfo.setText(data.getMessage());

    }
}
