package com.goldmedal.hrapp.ui.leftdrawer

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.databinding.RowImageViewBinding
import com.xwray.groupie.viewbinding.BindableItem

class MultipleImageItem(
    private val context: Context,
    private val imageUrl: String
): BindableItem<RowImageViewBinding>() {

    override fun bind(viewBinding: RowImageViewBinding, position: Int) {
        Glide.with(context)
            .load(imageUrl)
            .error(R.drawable.image_upload)
            .into(viewBinding.ivImage)

    }

    override fun getLayout(): Int = R.layout.row_image_view

    override fun initializeViewBinding(view: View): RowImageViewBinding = RowImageViewBinding.bind(view)
}