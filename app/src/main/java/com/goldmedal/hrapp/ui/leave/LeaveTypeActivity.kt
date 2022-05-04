package com.goldmedal.hrapp.ui.leave

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.goldmedal.hrapp.databinding.LeaveTypeActivityBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.leave_type_activity.*

@AndroidEntryPoint
class LeaveTypeActivity : AppCompatActivity(), ApiStageListener<Any>, LeaveTypeItem.OnLeaveTypeClickedListener {




    private val leaveTypeModel: LeaveViewModel by viewModels()

    private lateinit var leaveTypeBinding : LeaveTypeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        leaveTypeBinding = DataBindingUtil.setContentView(this, R.layout.leave_type_activity)
        leaveTypeBinding.viewmodel = leaveTypeModel

        leaveTypeModel.apiListener = this
        
        leaveTypeModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                leaveTypeModel.userId = user.UserID
                leaveTypeModel.leaveTypeData()

            }
        })



    }


    private fun List<LeaveTypeData?>.toLeaveType(): List<LeaveTypeItem?> {
        return this.map {
            LeaveTypeItem(it,this@LeaveTypeActivity)
        }
    }


    private fun bindUI(list: List<LeaveTypeData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveType())
        }


    }

    private fun initRecyclerView(toLeaveType: List<LeaveTypeItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveType)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter


            mAdapter.setOnItemClickListener { item, view ->
            }
        }
    }

    override fun onStarted(callFrom: String) {
        view_common.showProgressBar()

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        view_common?.hide()
        bindUI(_object as List<LeaveTypeData?>)

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
if(isNetworkError){
    view_common?.showNoInternet()
}else {
    view_common?.showNoData()
}

        root_layout?.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        root_layout?.snackbar(message)
    }

    override fun onLeaveTypeClicked(model: LeaveTypeData?) {
        val intent = intent
        intent.putExtra(ARG_LEAVE_TYPE, model)
        setResult(Activity.RESULT_OK, intent)
        finish()


    }

companion object {
     const val ARG_LEAVE_TYPE = "leave_type"
}
}
