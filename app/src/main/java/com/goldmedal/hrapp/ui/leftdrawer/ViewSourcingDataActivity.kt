package com.goldmedal.hrapp.ui.leftdrawer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.AddCompanyData
import com.goldmedal.hrapp.data.model.GetCompanyDetailsData
import com.goldmedal.hrapp.data.network.GlobalConstant
import com.goldmedal.hrapp.databinding.ActivityViewSourcingDataBinding
import com.goldmedal.hrapp.databinding.DialogViewImageMultipleBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.holiday_list_activity.*

@AndroidEntryPoint
class ViewSourcingDataActivity : AppCompatActivity(), ApiStageListener<Any>, CompanyDetailsItem.OnItemClickListener {
    private val sourcingDataViewModel: SourcingDataViewModel by viewModels()
    private lateinit var mBinding: ActivityViewSourcingDataBinding
    private var mUserId = 0
    private lateinit var mDialogViewImageMultipleBinding: DialogViewImageMultipleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_sourcing_data)

        mBinding.sourcingDataViewModel = sourcingDataViewModel
        sourcingDataViewModel.apiListener = this

        sourcingDataViewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                mUserId = user.UserID ?: 0
                user.UserID?.let { sourcingDataViewModel.getCompanyDetailsApi(it) }
            }
        })
    }

    override fun onStarted(callFrom: String) {
        mBinding.progressBar.start()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        mBinding.progressBar.stop()
        mBinding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        mBinding.rootLayout.snackbar(message)
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        mBinding.progressBar.stop()

        if (callFrom == GlobalConstant.GET_COMPANY_DETAILS_API) {
            bindUI(_object as List<GetCompanyDetailsData?>)
        } else if (callFrom == GlobalConstant.DELETE_COMPANY_DETAILS_API) {
            val addCompanyData = _object as List<AddCompanyData>
            val message = addCompanyData[0].StatusMessage
            mBinding.rootLayout.snackbar(message)
            sourcingDataViewModel.getCompanyDetailsApi(mUserId)
        }
    }

    private fun bindUI(list: List<GetCompanyDetailsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toCompanyDetailsData())
        }
    }

    private fun List<GetCompanyDetailsData?>.toCompanyDetailsData(): List<CompanyDetailsItem?> {
        return this.map {
            it?.let { it1 -> CompanyDetailsItem(it1, this@ViewSourcingDataActivity) }
        }
    }

    private fun initRecyclerView(toCompanyDetailsData: List<CompanyDetailsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toCompanyDetailsData)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun openImageViewDialog(list: List<String>) {
        mDialogViewImageMultipleBinding =
            DialogViewImageMultipleBinding.inflate(LayoutInflater.from(this))
        val dialog: AlertDialog = MaterialAlertDialogBuilder(this, R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
            .setView(mDialogViewImageMultipleBinding.root).show()
        dialog.setCancelable(false)

        bindDialogUI(list)

        mDialogViewImageMultipleBinding.tvClose.setOnClickListener { v -> dialog.dismiss() }
    }

    private fun bindDialogUI(list: List<String>?) = Coroutines.main {
        list?.let {
            initDialogRecyclerView(it.toImagesData())
        }
    }

    private fun List<String?>.toImagesData(): List<MultipleImageItem?> {
        return this.map {
            it?.let { it1 -> MultipleImageItem(this@ViewSourcingDataActivity, it1) }
        }
    }

    private fun initDialogRecyclerView(toImageData: List<MultipleImageItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toImageData)
        }

        mDialogViewImageMultipleBinding.rvList.apply {
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onItemClick(model: GetCompanyDetailsData?, isVisitingCard: Boolean) {
        val dataList = if (isVisitingCard) model?.VisitingCardImages?.split(",") else model?.ProductImages?.split(",")

        dataList?.let { openImageViewDialog(it) } ?: Toast.makeText(this, "No Images to show", Toast.LENGTH_SHORT).show()
    }

    override fun onDelete(model: GetCompanyDetailsData?, position: Int) {
        model?.let { sourcingDataViewModel.deleteCompanyDetailsApi(it.CompanyDetailsID, mUserId) }
    }

}