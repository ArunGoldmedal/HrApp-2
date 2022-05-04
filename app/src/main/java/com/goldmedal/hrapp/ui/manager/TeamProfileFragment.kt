package com.goldmedal.hrapp.ui.manager

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.data.model.MyTeamProfileData
import com.goldmedal.hrapp.databinding.FragmentTeamProfileBinding
import com.goldmedal.hrapp.ui.dashboard.home.HomeViewModel
import com.goldmedal.hrapp.util.formatNumber
import com.goldmedal.hrapp.util.getCurrentFiscalYear
import com.goldmedal.hrapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_MODEL_ITEM = "model_item"

@AndroidEntryPoint
class TeamProfileFragment : Fragment(), ApiStageListener<Any>{

    private var modelItem: MyTeamData? = null


private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentTeamProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modelItem = it.getParcelable(ARG_MODEL_ITEM)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentTeamProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.apiListener = this


                viewModel.getMyTeamProfile(modelItem?.Slno, getCurrentFiscalYear()) // "2020-2021"

    }
    private fun bindUI(data: MyTeamProfileData?) {
        binding.tvUserName.text = data?.EmployeeName
        binding.tvUserContactNo.text = data?.ContactNumber
        binding.tvUserPost.text = data?.DepartmentName + getString(R.string.dash) + data?.DesignationName

        binding.tvEmployeeCode.text = data?.EmployeeCode
        binding.tvOfficeEmail.text = data?.officeEmail
        binding.tvJoiningDate.text = data?.joiningDate
        binding.tvDOB.text = data?.DateOfBirth
        binding.tvReportingPerson.text = data?.ReportingPerson
        binding.tvHomeAddress.text = data?.HomeAddress
        binding.tvOfficeAddress.text = data?.OfficeAddress
        binding.tvBranch.text = data?.BranchName
        binding.txtTakenLeaves.text = formatNumber(data?.DebitLeave)
        binding.txtRemLeaves.text = formatNumber(data?.AvailableLeave)
        binding.txtTotalLeaves.text = formatNumber(data?.CreditLeave)
        binding.tvLocality.text = "${getString(R.string.locality)} ${data?.Location ?: "-"}"
        binding.tvSubLocality.text = "${getString(R.string.sub_locality)} ${data?.Sublocation ?: "-"}"


        if (!data?.ContactNumber.isNullOrEmpty()) {
            binding.tvUserContactNo.paintFlags = binding.tvUserContactNo.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        Glide.with(this)
                .load(modelItem?.ProfileImg)
                .fitCenter()
                .placeholder(R.drawable.ic_profile)
                .into(binding.imageEmployee)



        binding.tvUserContactNo.setOnClickListener {
            if (!data?.ContactNumber.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = (Uri.parse("tel:" + data?.ContactNumber))
                context?.startActivity(intent)
            }
        }
    }


    override fun onStarted(callFrom: String) {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.progressBar.stop()
        val data = _object as List<MyTeamProfileData?>?
        bindUI(data?.get(0))
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        binding.progressBar.stop()
        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        @JvmStatic
        fun newInstance(item: MyTeamData?) =
                TeamProfileFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_MODEL_ITEM, item)
                    }
                }
    }

}