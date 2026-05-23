package com.goldmedal.hrapp.data.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.CustomBean
import com.goldmedal.hrapp.data.model.viewholder.CustomPageViewHolder
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class IntroAdapter(private val context: Context) : BaseBannerAdapter<CustomBean>() {

    //var mOnSubViewClickListener: CustomPageViewHolder.OnSubViewClickListener? = null

    /*override fun onBind(holder: CustomPageViewHolder, data: CustomBean, position: Int, pageSize: Int) {
        holder.bindData(data, position, pageSize)
    }*/

    /*override fun createViewHolder(itemView: View, viewType: Int): CustomPageViewHolder? {
        //  customPageViewHolder.setOnSubViewClickListener(mOnSubViewClickListener)
        return CustomPageViewHolder(itemView,context)
    }*/

    override fun bindData(
        holder: BaseViewHolder<CustomBean>?,
        data: CustomBean?,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data, position, pageSize)
    }

    override fun createViewHolder(
        parent: ViewGroup,
        itemView: View,
        viewType: Int
    ): BaseViewHolder<CustomBean> {
        return CustomPageViewHolder(itemView, context)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_intro_view
    }
}