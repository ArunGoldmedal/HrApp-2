package com.goldmedal.hrapp.data.adapters

import android.content.Context
import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.CustomBean
import com.goldmedal.hrapp.data.model.viewholder.CustomPageViewHolder
import com.zhpan.bannerview.BaseBannerAdapter

class IntroAdapter(private val context: Context) : BaseBannerAdapter<CustomBean, CustomPageViewHolder>() {

    //var mOnSubViewClickListener: CustomPageViewHolder.OnSubViewClickListener? = null

    override fun onBind(holder: CustomPageViewHolder, data: CustomBean, position: Int, pageSize: Int) {
        holder.bindData(data, position, pageSize)
    }

    override fun createViewHolder(itemView: View, viewType: Int): CustomPageViewHolder? {
        //  customPageViewHolder.setOnSubViewClickListener(mOnSubViewClickListener)
        return CustomPageViewHolder(itemView,context)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_intro_view
    }
}