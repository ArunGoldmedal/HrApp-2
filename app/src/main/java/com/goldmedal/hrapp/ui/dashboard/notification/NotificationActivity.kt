package com.goldmedal.hrapp.ui.dashboard.notification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.NotificationFeeds
import com.goldmedal.hrapp.databinding.ActivityNotificationBinding
import com.goldmedal.hrapp.ui.dashboard.TransitionsActivity
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_notification.*

@AndroidEntryPoint
class NotificationActivity : TransitionsActivity(), ApiStageListener<Any> {




private val viewModel: NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        binding.viewmodel = viewModel

        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if(user != null){
                viewModel.fetchNotifications(user.UserID)
            }
        })

    }

    private fun List<NotificationFeeds?>.toLeaveRecord(): List<NotificationItem?> {
        return this.map {
            NotificationItem(it)
        }
    }

    private fun initRecyclerView(toLeaveRecord: List<NotificationItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }
    private fun bindUI(list: List<NotificationFeeds?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveRecord())
        }
    }
    override fun onStarted(callFrom: String) {
        view_common?.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        val data = _object as List<NotificationFeeds?>
        if (data.isNullOrEmpty()) {
            view_common?.showNoData()
        } else {
            view_common?.hide()
        }

        bindUI(data)

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if(isNetworkError){
            view_common?.showNoInternet()
        }else{
            view_common?.showNoData()
        }
        root_layout?.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        root_layout?.snackbar(message)
    }
}
