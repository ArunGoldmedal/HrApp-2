package com.goldmedal.hrapp.data.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.data.db.entities.HolidayData;
import com.goldmedal.hrapp.data.model.viewholder.HolidayViewHolder;
import com.goldmedal.hrapp.data.model.viewholder.NoDataHolidayHolder;
import com.goldmedal.hrapp.data.network.GlobalConstant;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class HolidayAdapter extends BaseBannerAdapter<HolidayData> {

    private int roundCorner;

    public HolidayAdapter(int roundCorner) {
        this.roundCorner = roundCorner;
    }


    /*@Override
    protected void onBind(BaseViewHolder<HolidayData> holder, HolidayData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }*/
    /*@Override
    public BaseViewHolder<HolidayData> createViewHolder(View itemView, int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return new NoDataHolidayHolder(itemView, roundCorner);
        }
        return new HolidayViewHolder(itemView, roundCorner);
    }*/

    @Override
    public BaseViewHolder<HolidayData> createViewHolder(@NonNull ViewGroup parent, View itemView, int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return new NoDataHolidayHolder(itemView, roundCorner);
        }
        return new HolidayViewHolder(itemView, roundCorner);
    }

    @Override
    public int getViewType(int position) {
        return mList.get(position).getViewType();
    }

    @Override
    protected void bindData(BaseViewHolder<HolidayData> holder, HolidayData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return R.layout.info_view;
        }
        return R.layout.item_slide_mode;
    }
}
