package com.goldmedal.hrapp.ui.leave

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.databinding.RequestsArchiveFragmentBinding
import com.goldmedal.hrapp.util.Coroutines
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*

class RequestsArchiveFragment : Fragment() {

    private var _binding: RequestsArchiveFragmentBinding? = null
    private val binding get() = _binding!!


    private var requestsList: ArrayList<LeaveRequestsData>? = null

    private var isError: Boolean = false


    companion object {

        fun newInstance(item: MutableList<LeaveRequestsData?>, error: Boolean): RequestsArchiveFragment {

            val fragment = RequestsArchiveFragment()
            val args = Bundle()
            args.putParcelableArrayList("requestsList", item as ArrayList<out Parcelable>)
            args.putBoolean("error", error)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = RequestsArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requestsList = arguments?.getParcelableArrayList("requestsList")
        isError = arguments?.getBoolean("error", false) ?: false


        if (!isError) {
            if (requestsList.isNullOrEmpty()) {
                binding.viewCommon.showNoData()
            } else {
                binding.viewCommon.hide()
            }
        }
        bindUI(requestsList)




        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })

    }

    private fun List<LeaveRequestsData?>.toLeaveRequests(): List<RequestsArchiveItem?> {
        return this.map {
            RequestsArchiveItem(it,requireContext())
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
    private fun filter(text: String) {
        val filteredNames = requestsList?.filter {
            (it.EmployeeName?.toLowerCase(Locale.getDefault())?.contains(text.toLowerCase(Locale.getDefault())) == true)
        }
        filterUI(filteredNames)
    }

    private fun filterList(data: List<RequestsArchiveItem?>){
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }
        binding.rvList.apply {
            adapter = mAdapter
        }
        if (!isError) {
            if (data.isEmpty()) {
                binding.viewCommon.showNoData()
            } else {
                binding.viewCommon.hide()
            }
        }
    }

    private fun initRecyclerView(toLeaveRecord: List<RequestsArchiveItem?>) {
       val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
