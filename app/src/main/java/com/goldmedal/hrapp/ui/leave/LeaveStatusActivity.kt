package com.goldmedal.hrapp.ui.leave


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.goldmedal.hrapp.data.model.RespondLeavesData
import com.goldmedal.hrapp.databinding.LeaveStatusActivityBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.getCurrentFiscalYear
import com.goldmedal.hrapp.util.snackbar
import com.goldmedal.hrapp.util.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class LeaveStatusActivity : AppCompatActivity(), ApiStageListener<Any>,  LeaveRecordItem.OnCancelClickedListener {
    private val leaveModel: LeaveViewModel by viewModels()
    private lateinit var leaveStatusActivityBinding: LeaveStatusActivityBinding
    private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        leaveStatusActivityBinding = DataBindingUtil.setContentView(this, R.layout.leave_status_activity)
        leaveStatusActivityBinding.viewmodel = leaveModel

        initSpinner()
        leaveModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                leaveModel.leaveRecordData(user.UserID, getCurrentFiscalYear())
            }
        })

        leaveModel.apiListener = this
    }

    private fun initSpinner() {


        val fiscalYear = getCurrentFiscalYear()
        val previousYearSplit = fiscalYear.split("-")
        val previousYear = "${previousYearSplit[0].toInt() - 1}-${previousYearSplit[1].toInt() - 1}"
        val penultimateYear = "${previousYearSplit[0].toInt() - 2}-${previousYearSplit[1].toInt() - 2}"

        val dataset: List<String> = LinkedList(listOf(fiscalYear, previousYear, penultimateYear))
        leaveStatusActivityBinding.spinnerSelectYear.attachDataSource(dataset)

        leaveStatusActivityBinding.spinnerSelectYear.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item: String = parent.getItemAtPosition(position) as String
                    leaveModel.getLoggedInUser().observe(this, Observer { user ->
                        if (user != null) {
                            leaveModel.leaveRecordData(user.UserID, item)
                        }

                    })
                }
    }


    private fun List<LeaveRecordData?>.toLeaveRecord(): List<LeaveRecordItem?> {
        return this.map {
            LeaveRecordItem(it, this@LeaveStatusActivity, this@LeaveStatusActivity)
        }
    }


    private fun bindUI(list: List<LeaveRecordData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLeaveRecord())
        }
    }

    private fun initRecyclerView(toLeaveRecord: List<LeaveRecordItem?>) {
        mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        leaveStatusActivityBinding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted(callFrom: String) {
        leaveStatusActivityBinding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        if (callFrom == "leaveRecord") {

            val data = _object as List<LeaveRecordData?>
            if (data.isNullOrEmpty()) {
                leaveStatusActivityBinding.viewCommon.showNoData()
            }else{
                leaveStatusActivityBinding.viewCommon.hide()
            }
            bindUI(data)

        } else if (callFrom.equals("leaveCancel")) {

            leaveStatusActivityBinding.viewCommon.hide()
            val data = _object as List<RespondLeavesData>
            data[0].StatusMessage?.let { toast(it) }
            finish()
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        if (isNetworkError) {
            leaveStatusActivityBinding.viewCommon.showNoInternet()
        } else {
            leaveStatusActivityBinding.viewCommon.showNoData()
        }
        bindUI(ArrayList())
        leaveStatusActivityBinding.rootLayout.snackbar(message)
    }

    override fun onCancelClicked(item: LeaveRecordData?) {


        MaterialAlertDialogBuilder(this@LeaveStatusActivity)
                .setMessage(resources.getString(R.string.supporting_text))
                .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which ->

                }
                .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
                    leaveModel.getLoggedInUser().observe(this, Observer { user ->
                        if (user != null) {
                            leaveModel.applyLeaveId = item?.ApplyLeaveID
                            leaveModel.cancelLeave(user.UserID)
                        }

                    })
                }
                .show()


    }

    override fun onValidationError(message: String, callFrom: String) {
        leaveStatusActivityBinding.rootLayout.snackbar(message)
    }

}
