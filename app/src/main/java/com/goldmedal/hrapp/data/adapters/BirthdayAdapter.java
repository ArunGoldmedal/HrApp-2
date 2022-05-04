package com.goldmedal.hrapp.data.adapters;

import android.view.View;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.data.db.entities.BirthdayData;
import com.goldmedal.hrapp.data.model.viewholder.BirthdayViewHolder;
import com.goldmedal.hrapp.data.model.viewholder.NoDataBirthdayHolder;
import com.goldmedal.hrapp.data.network.GlobalConstant;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class BirthdayAdapter extends BaseBannerAdapter<BirthdayData, BaseViewHolder<BirthdayData>> {

    private int roundCorner;

    public BirthdayAdapter(int roundCorner) {
        this.roundCorner = roundCorner;
    }


    @Override
    protected void onBind(BaseViewHolder<BirthdayData> holder, BirthdayData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public BaseViewHolder<BirthdayData> createViewHolder(View itemView, int viewType) {

        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return new NoDataBirthdayHolder(itemView, roundCorner);
        }
        return new BirthdayViewHolder(itemView, roundCorner);


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
        return R.layout.item_layout;
    }
}

