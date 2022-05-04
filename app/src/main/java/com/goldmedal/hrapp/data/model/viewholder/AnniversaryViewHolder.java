package com.goldmedal.hrapp.data.model.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.common.CornerImageView;
import com.goldmedal.hrapp.data.db.entities.AnniversaryData;
import com.zhpan.bannerview.BaseViewHolder;


public class AnniversaryViewHolder extends BaseViewHolder<AnniversaryData> {

    public AnniversaryViewHolder(@NonNull View itemView, int roundCorner) {
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

        AppCompatTextView txtEmployee = findView(R.id.txt_employee);
        AppCompatTextView txtBirthDayDate = findView(R.id.txt_message);

        ImageView imageView = findView(R.id.avatarImageView);


        int avatar = R.drawable.male_avatar;


        try {
            if (!data.getGender().equals("1")) {
                avatar = R.drawable.female_avatar;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




        Glide.with(imageView)
                .load(data.getProfilePicture())
                .placeholder(avatar).into(imageView);

        txtEmployee.setText(data.getEmployeeName());
        txtBirthDayDate.setText(data.getMessage());
    }
}