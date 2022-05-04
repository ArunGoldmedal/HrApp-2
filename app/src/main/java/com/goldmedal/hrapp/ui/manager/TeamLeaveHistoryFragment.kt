package com.goldmedal.hrapp.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ColorTemplate
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.data.model.LeaveRecordData
import com.goldmedal.hrapp.databinding.FragmentTeamLeaveHistoryBinding
import com.goldmedal.hrapp.ui.leave.LeaveViewModel
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.getCurrentFiscalYear
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.*

@AndroidEntryPoint
class TeamLeaveHistoryFragment : Fragment(), ApiStageListener<Any> {
    private var modelItem: MyTeamData? = null



private val leaveModel: LeaveViewModel by viewModels()

    private var _binding: FragmentTeamLeaveHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modelItem = it.getParcelable(ARG_MODEL_ITEM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentTeamLeaveHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        leaveModel.apiListener = this

        initSpinner()
        leaveModel.leaveRecordData(modelItem?.Slno, getCurrentFiscalYear())

    }

    //RecyclerView Data source
    private fun List<LeaveRecordData?>.toData(): List<TeamLeaveRecordItem?> {
        return this.map {
            TeamLeaveRecordItem(it, requireContext(), `2DChipColors`)
        }
    }

    private fun initRecyclerView(data: List<TeamLeaveRecordItem?>) {
        mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<LeaveRecordData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toData())
        }
    }

    private fun initSpinner() {


        val fiscalYear = getCurrentFiscalYear()
        val previousYearSplit = fiscalYear.split("-")
        val previousYear = "${previousYearSplit[0].toInt() - 1}-${previousYearSplit[1].toInt() - 1}"
        val penultimateYear = "${previousYearSplit[0].toInt() - 2}-${previousYearSplit[1].toInt() - 2}"



        val dataset: List<String> = LinkedList(listOf(fiscalYear, previousYear, penultimateYear))
        binding.spinnerSelectYear.attachDataSource(dataset)

        binding.spinnerSelectYear.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item: String = parent.getItemAtPosition(position) as String

                    leaveModel.leaveRecordData(modelItem?.Slno, item)

                }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()
        bindUI(_object as List<LeaveRecordData?>)
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showNoDataImage()
        }
        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }

    companion object {
        private const val ARG_MODEL_ITEM = "model_item"
        private val `2DChipColors`: Array<IntArray> = arrayOf(
                intArrayOf(ColorTemplate.rgb("#c5cae9"), ColorTemplate.rgb("#65499c")), //INDIGO
                intArrayOf(ColorTemplate.rgb("#b2ebf2"), ColorTemplate.rgb("#009faf")), //CYAN
                intArrayOf(ColorTemplate.rgb("#c8e6c9"), ColorTemplate.rgb("#519657")), //GREEN
                intArrayOf(ColorTemplate.rgb("#f8bbd0"), ColorTemplate.rgb("#ba2d65")), //PINK
                intArrayOf(ColorTemplate.rgb("#bbdefb"), ColorTemplate.rgb("#2286c3")), //BLUE
                intArrayOf(ColorTemplate.rgb("#ffecb3"), ColorTemplate.rgb("#c8a415")), //AMBER
                intArrayOf(ColorTemplate.rgb("#f0f4c3"), ColorTemplate.rgb("#a8b545")) //LIME
        )

        @JvmStatic
        fun newInstance(item: MyTeamData?) =
                TeamLeaveHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_MODEL_ITEM, item)
                    }
                }
    }
}