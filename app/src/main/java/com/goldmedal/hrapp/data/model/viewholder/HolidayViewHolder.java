package com.goldmedal.hrapp.data.model.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.common.CornerImageView;
import com.goldmedal.hrapp.data.db.entities.HolidayData;
import com.zhpan.bannerview.BaseViewHolder;

public class HolidayViewHolder extends BaseViewHolder<HolidayData> {

    public HolidayViewHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
        CornerImageView imageView = findView(R.id.banner_image);
        imageView.setRoundCorner(roundCorner);

    }

//    @Override
//    public void bindData(Integer data, int position, int pageSize) {
//        setImageResource(R.id.banner_image, data);
//    }

    @Override
    public void bindData(HolidayData data, int position, int pageSize) {
        CornerImageView imageView = findView(R.id.banner_image);

        if (data.getHolidayName() != null) {
            String holidayName = data.getHolidayName().toLowerCase();

            //REPUBLIC DAY
            if (holidayName.contains("republic")) {
                imageView.setImageResource(R.drawable.republic_day);
            }
            //HOLI
            else if (holidayName.contains("holi")) {
                imageView.setImageResource(R.drawable.holi);
            }
            //LABOR DAY
            else if (holidayName.contains("labor") || holidayName.contains("labour")) {
                imageView.setImageResource(R.drawable.labor);
            }
            //Rakshabandhan
            else if (holidayName.contains("rakshabandhan") || holidayName.contains("rakhi")) {
                imageView.setImageResource(R.drawable.rakhi);
            }
            //Independence Day
            else if (holidayName.contains("independence")) {
                imageView.setImageResource(R.drawable.independence_day);
            }
            //Ganesh Chaturthi
            else if (holidayName.contains("ganesh")) {
                imageView.setImageResource(R.drawable.ganesh_chaturthi);
            }
            //Gandhi Jayanti
            else if (holidayName.contains("gandhi")) {
                imageView.setImageResource(R.drawable.gandhi_jayanti);
            }
            //Dussehra
            else if (holidayName.contains("dushera") || holidayName.contains("duss")) {
                imageView.setImageResource(R.drawable.dushera);
            }
            //Diwali
            else if (holidayName.contains("diwali") || holidayName.contains("deep") || holidayName.contains("divali") || holidayName.contains("dipa")) {
                imageView.setImageResource(R.drawable.diwali);
            }
            //Bhai Dooj
            else if (holidayName.contains("bhai dooj")) {
                imageView.setImageResource(R.drawable.bhai_dooj);
            } else {
                imageView.setImageResource(R.drawable.holiday_default);
            }

//    switch (holidayName) {
//
//        case "7","8":
//
//            imageView.setImageResource(R.drawable.republic_day);
//            break;
//
//        case 8:
//            imageView.setImageResource(R.drawable.holi);
//            break;
//
//
//        case 9:
//            imageView.setImageResource(R.drawable.labor);
//
//            break;
//
//
//        case 10:
//            imageView.setImageResource(R.drawable.rakhi);
//            break;
//
//
//
//        case 11:
//            imageView.setImageResource(R.drawable.independence_day);
//            break;
//
//
//        case 12:
//
//            imageView.setImageResource(R.drawable.ganesh_chaturthi);
//            break;
//
//        case 13:
//            imageView.setImageResource(R.drawable.gandhi_jayanti);
//            break;
//
//        case 14:
//            imageView.setImageResource(R.drawable.dushera);
//
//            break;
//
//        case 15:
//            imageView.setImageResource(R.drawable.diwali);
//
//            break;
//
//        case 16:
//            imageView.setImageResource(R.drawable.bhai_dooj);
//            break;
//
//
//        //Default
//        default:
//            imageView.setImageResource(R.drawable.holiday_default);
//            break;

        } else {
            imageView.setImageResource(R.drawable.holiday_default);
        }


        //Glide.with(imageView).load(data.getDescription()).placeholder(R.drawable.gandhi_jayanti).into(imageView);
    }
}
