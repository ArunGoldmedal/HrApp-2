package com.goldmedal.hrapp.ui.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.databinding.MyTeamItemBinding
import com.xwray.groupie.viewbinding.BindableItem
class MyTeamItem(
        private val data: MyTeamData?,
        private val context: Context
) : BindableItem<MyTeamItemBinding>() {


    override fun bind(viewBinding: MyTeamItemBinding, position: Int) {

        viewBinding.apply {
            textViewName.text = data?.EmployeeName


            Glide.with(context)
                    .load(data?.ProfileImg)
                    .fitCenter()
                    .placeholder(R.drawable.ic_profile)
                    .into(imvProfilePic)

            itemView.setOnClickListener {
                TeamProfileHistoryActivity.start(context,data)
            }

        }
    }
    override fun getLayout() = R.layout.my_team_item
    override fun initializeViewBinding(view: View) = MyTeamItemBinding.bind(view)
}