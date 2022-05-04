package com.goldmedal.hrapp.data.adapters;

import android.view.View;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.data.db.entities.HolidayData;
import com.goldmedal.hrapp.data.model.viewholder.HolidayViewHolder;
import com.goldmedal.hrapp.data.model.viewholder.NoDataHolidayHolder;
import com.goldmedal.hrapp.data.network.GlobalConstant;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class HolidayAdapter extends BaseBannerAdapter<HolidayData, BaseViewHolder<HolidayData>> {

    private int roundCorner;

    public HolidayAdapter(int roundCorner) {
        this.roundCorner = roundCorner;
    }


    @Override
    protected void onBind(BaseViewHolder<HolidayData> holder, HolidayData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }
    @Override
    public BaseViewHolder<HolidayData> createViewHolder(View itemView, int viewType) {
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
    public int getLayoutId(int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return R.layout.info_view;
        }
        return R.layout.item_slide_mode;
    }
}
