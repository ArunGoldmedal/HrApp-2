package com.goldmedal.hrapp.ui.dashboard.leave


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ColorTemplate
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.data.repositories.ILeaveListener
import com.goldmedal.hrapp.databinding.FragmentLeaveRequestsBinding
import com.goldmedal.hrapp.ui.leave.LeaveRequestsItem
import com.goldmedal.hrapp.ui.leave.LeaveViewModel
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IndexOutOfBoundsException
import java.util.*

@AndroidEntryPoint
class LeaveRequestsFragment : Fragment(), ApiStageListener<Any>, ILeaveListener {
    private lateinit var leaveRequestsActivityBinding: FragmentLeaveRequestsBinding
    private val leaveModel: LeaveViewModel by viewModels()


    private var requestsList: List<LeaveRequestsData?>? = null
    private val `2DChipColors`: Array<IntArray> = arrayOf(
            intArrayOf(ColorTemplate.rgb("#c5cae9"), ColorTemplate.rgb("#65499c")), //INDIGO
            intArrayOf(ColorTemplate.rgb("#b2ebf2"), ColorTemplate.rgb("#009faf")), //CYAN
            intArrayOf(ColorTemplate.rgb("#c8e6c9"), ColorTemplate.rgb("#519657")), //GREEN
            intArrayOf(ColorTemplate.rgb("#f8bbd0"), ColorTemplate.rgb("#ba2d65")), //PINK
            intArrayOf(ColorTemplate.rgb("#bbdefb"), ColorTemplate.rgb("#2286c3")), //BLUE
            intArrayOf(ColorTemplate.rgb("#ffecb3"), ColorTemplate.rgb("#c8a415")), //AMBER
            intArrayOf(ColorTemplate.rgb("#f0f4c3"), ColorTemplate.rgb("#a8b545")) //LIME
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        leaveRequestsActivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_leave_requests, container, false)
        return leaveRequestsActivityBinding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leaveRequestsActivityBinding.viewmodel = leaveModel

        leaveModel.apiListener = this


        leaveModel.getLoggedInUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                leaveModel.getLeaveRequests(user.UserID, 1)
            }
        }



        leaveRequestsActivityBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })
    }

    private fun initRecyclerView(leaveRequests: List<LeaveRequestsItem?>) {
//initialize groupie's group adapter class and add the list of items
        val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(leaveRequests)
        }


        //set up the layout manager and set the adapter
        leaveRequestsActivityBinding.rvList.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))
            adapter = groupAdapter
        }


    }

    private fun bindUI(list: List<LeaveRequestsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveRequests())
        }
    }

    private fun filterUI(list: List<LeaveRequestsData?>?) = Coroutines.main {
        list?.let {
            filterList(it.toLeaveRequests())
        }
    }

    private fun filterList(data: List<LeaveRequestsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }
        leaveRequestsActivityBinding.rvList.apply {
            adapter = mAdapter
        }
        if (data.isEmpty()) {
            leaveRequestsActivityBinding.viewCommon.showNoData()
        } else {
            leaveRequestsActivityBinding.viewCommon.hide()
        }

    }

    private fun List<LeaveRequestsData?>.toLeaveRequests(): List<LeaveRequestsItem?> {
        return this.map {
            LeaveRequestsItem(it, `2DChipColors`, requireActivity() as? AppCompatActivity, this@LeaveRequestsFragment)
        }
    }

    private fun filter(text: String) {
        Coroutines.io {
            val filteredNames = requestsList?.filter {
                (it?.EmployeeName?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true)
            }
            filterUI(filteredNames)
        }
    }

    override fun onStarted(callFrom: String) {
        leaveRequestsActivityBinding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        requestsList = _object as List<LeaveRequestsData?>


        if (requestsList.isNullOrEmpty()) {
            leaveRequestsActivityBinding.viewCommon.showNoData()
        } else {
            leaveRequestsActivityBinding.viewCommon.hide()
        }

        bindUI(requestsList)
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            leaveRequestsActivityBinding.viewCommon.showNoInternet()
        } else {
            leaveRequestsActivityBinding.viewCommon.showNoData()
        }

        leaveRequestsActivityBinding.rootLayout.snackbar(message)
    }

    override fun observeRequests(removeAt: Int?) {
        removeAt?.let {
            try {
                val adapter: GroupAdapter<GroupieViewHolder> = leaveRequestsActivityBinding.rvList.adapter as GroupAdapter<GroupieViewHolder>
            adapter.removeGroupAtAdapterPosition(it)
            if (adapter.itemCount == 0) {
                leaveRequestsActivityBinding.viewCommon.showNoData()
            }
            Log.d("count", adapter.itemCount.toString())
        } catch (e: IndexOutOfBoundsException) {
        }
        }
    }

    override fun onValidationError(message: String, callFrom: String) {
        leaveRequestsActivityBinding.rootLayout.snackbar(message)
    }
}