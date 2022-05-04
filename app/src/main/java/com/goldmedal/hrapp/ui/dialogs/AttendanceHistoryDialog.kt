package com.goldmedal.hrapp.ui.dialogs


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.AttendanceHistoryData
import com.goldmedal.hrapp.databinding.AttendanceHistoryDialogBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.activityContext
import com.goldmedal.hrapp.util.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

import java.util.*

@AndroidEntryPoint
class AttendanceHistoryDialog : DialogFragment(), ApiStageListener<Any> {

private val viewModel: AttendanceHistoryViewModel by viewModels()
    private lateinit var attBinding: AttendanceHistoryDialogBinding
    private var attendanceHistoryData: List<AttendanceHistoryData?> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onResume() {
        super.onResume()

        val metrics = requireContext().resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.90).toInt()
        val screenHeight = (metrics.heightPixels * 0.80).toInt()
        dialog?.window?.setLayout(screenWidth, screenHeight)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        attBinding = DataBindingUtil.inflate(inflater, R.layout.attendance_history_dialog, container, false)
        return attBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attBinding.viewmodel = viewModel
        viewModel.apiListener = this




        viewModel.getLoggedInUser().observe(viewLifecycleOwner, { user ->
            if (user != null) {
                viewModel.userId = user.UserID
                viewModel.attendanceStatus = arguments?.getInt("attendanceStatus", -1) ?: -1
                if (viewModel.attendanceStatus == 2) {
                    attBinding.txtPunchHeader.text = resources.getString(R.string.str_present_employee_attendance)
                } else if (viewModel.attendanceStatus == 3) {
                    attBinding.txtPunchHeader.text = resources.getString(R.string.str_absent_employee_attendance)
                }
                viewModel.getAttendanceHistory()
            }
        })

        attBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString())
            }
        })
    }
    private fun filter(text: String) {

        //looping through existing elements
        Coroutines.io {
            val filteredNames = attendanceHistoryData.filter {
                (it?.Username?.toLowerCase(Locale.getDefault())?.contains(text.toLowerCase(Locale.getDefault())) == true)
            }
            filterUI(filteredNames)
        }
    }

    private fun List<AttendanceHistoryData?>.toAttendanceHistory(): List<AttendanceHistoryItem?> {
        return this.map {
            AttendanceHistoryItem(it, context = activityContext(requireContext()) as? AppCompatActivity) //this@LeaveRecordActivity
        }
    }


    private fun bindUI(list: List<AttendanceHistoryData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toAttendanceHistory())
        }
    }

    private fun filterUI(list: List<AttendanceHistoryData?>?) = Coroutines.main {
        list?.let {
            filterList(it.toAttendanceHistory())
        }


    }

    private fun filterList(toAttendanceHistory: List<AttendanceHistoryItem?>){
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAttendanceHistory)
        }
        attBinding.rvList.apply {
            adapter = mAdapter
        }

        if (toAttendanceHistory.isEmpty()) {
            attBinding.viewCommon.showNoData()
        }else{
            attBinding.viewCommon.hide()
        }
    }

    private fun initRecyclerView(toAttendanceHistory: List<AttendanceHistoryItem?>) {
       val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAttendanceHistory)
        }

        attBinding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted(callFrom: String) {
        attBinding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {


        val data = _object as List<AttendanceHistoryData?>

        attendanceHistoryData = data
        if (data.isNullOrEmpty()) {
            attBinding.viewCommon.showNoData()
        } else {
            attBinding.viewCommon.hide()
        }

        bindUI(data)


    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            attBinding.viewCommon.showNoInternet()
        } else {
            attBinding.viewCommon.showNoData()
        }
        context?.toast(message)
    }





    companion object {
        fun newInstance() = AttendanceHistoryDialog()
    }

    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}