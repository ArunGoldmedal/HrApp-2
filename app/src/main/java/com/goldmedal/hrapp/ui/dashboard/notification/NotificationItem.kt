package com.goldmedal.hrapp.ui.dashboard.notification

import android.view.View
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.NotificationFeeds
import com.goldmedal.hrapp.databinding.NotificationRowBinding
import com.xwray.groupie.viewbinding.BindableItem


class NotificationItem(private val feeds: NotificationFeeds?) : BindableItem<NotificationRowBinding>(){

    override fun bind(viewBinding: NotificationRowBinding, position: Int) {
        viewBinding.feeds = feeds
    }


    override fun getLayout() = R.layout.notification_row
    override fun initializeViewBinding(view: View): NotificationRowBinding = NotificationRowBinding.bind(view)


}