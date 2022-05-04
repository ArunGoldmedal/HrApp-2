package com.goldmedal.hrapp.ui.leave

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ColorTemplate.rgb
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.data.repositories.ILeaveListener
import com.goldmedal.hrapp.databinding.ActivityLeaveRequestsBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_leave_requests.*

@AndroidEntryPoint
class LeaveRequestsActivity : AppCompatActivity(), ApiStageListener<Any>, ILeaveListener {



    private val leaveModel: LeaveViewModel by viewModels()

    private val `2DChipColors`: Array<IntArray> = arrayOf(
            intArrayOf(rgb("#c5cae9"), rgb("#65499c")), //INDIGO
            intArrayOf(rgb("#b2ebf2"), rgb("#009faf")), //CYAN
            intArrayOf(rgb("#c8e6c9"), rgb("#519657")), //GREEN
            intArrayOf(rgb("#f8bbd0"), rgb("#ba2d65")), //PINK
            intArrayOf(rgb("#bbdefb"), rgb("#2286c3")), //BLUE
            intArrayOf(rgb("#ffecb3"), rgb("#c8a415")), //AMBER
            intArrayOf(rgb("#f0f4c3"), rgb("#a8b545")) //LIME
    )


    private lateinit var leaveRequestsActivityBinding: ActivityLeaveRequestsBinding
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        leaveRequestsActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_leave_requests)
        leaveRequestsActivityBinding.viewmodel = leaveModel

        leaveModel.apiListener = this


        leaveModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {

           leaveModel.getLeaveRequests(user.UserID,1)

            }

        })


    }

    private fun initRecyclerView(leaveRequests: List<LeaveRequestsItem?>) {
//initialize groupie's group adapter class and add the list of items
        groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(leaveRequests)
        }


        //set up the layout manager and set the adapter
        rvList?.apply {
            layoutManager = LinearLayoutManager(this@LeaveRequestsActivity)
            addItemDecoration(DividerItemDecoration(this@LeaveRequestsActivity, DividerItemDecoration.VERTICAL))
            adapter = groupAdapter
        }



    }

    private fun bindUI(list: List<LeaveRequestsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveRequests())
        }


    }

    private fun List<LeaveRequestsData?>.toLeaveRequests(): List<LeaveRequestsItem?> {
        return this.map {
            LeaveRequestsItem(it, `2DChipColors`, this@LeaveRequestsActivity, this@LeaveRequestsActivity)
        }
    }

    override fun onStarted(callFrom: String) {
        progress_bar?.start()

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        bindUI(_object as List<LeaveRequestsData?>)
        progress_bar?.stop()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        root_layout?.snackbar(message)
    }

    override fun observeRequests(removeAt: Int?) {

        removeAt?.let { groupAdapter.removeGroup(it) }

    }

    override fun onValidationError(message: String, callFrom: String) {}


}