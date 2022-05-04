package com.goldmedal.hrapp.data.adapters;

import android.view.View;

import com.goldmedal.hrapp.R;
import com.goldmedal.hrapp.data.db.entities.AnniversaryData;
import com.goldmedal.hrapp.data.model.viewholder.AnniversaryViewHolder;
import com.goldmedal.hrapp.data.model.viewholder.NoDataAnniversaryHolder;
import com.goldmedal.hrapp.data.network.GlobalConstant;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;


public class AnniversaryAdapter extends BaseBannerAdapter<AnniversaryData, BaseViewHolder<AnniversaryData>> {

    private int roundCorner;

    public AnniversaryAdapter(int roundCorner) {
        this.roundCorner = roundCorner;
    }


    @Override
    protected void onBind(BaseViewHolder<AnniversaryData> holder, AnniversaryData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public BaseViewHolder<AnniversaryData> createViewHolder(View itemView, int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return new NoDataAnniversaryHolder(itemView, roundCorner);
        }
        return new AnniversaryViewHolder(itemView, roundCorner);
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


