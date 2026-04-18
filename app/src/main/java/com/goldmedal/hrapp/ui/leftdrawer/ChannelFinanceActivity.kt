package com.goldmedal.hrapp.ui.leftdrawer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.goldmedal.hrapp.BaseActivity
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.model.ChannelFinanceDealerItem
import com.goldmedal.hrapp.data.model.DealerWiseData
import com.goldmedal.hrapp.data.model.NotificationFeeds
import com.goldmedal.hrapp.data.model.UpdateCNAmountItem
import com.goldmedal.hrapp.data.model.viewholder.SearchableListViewHolder
import com.goldmedal.hrapp.databinding.ActivityChannelFinanceBinding
import com.goldmedal.hrapp.databinding.DialogSearchableListBinding
import com.goldmedal.hrapp.databinding.RowSearchableListBinding
import com.goldmedal.hrapp.util.BaseGenericRecyclerViewAdapter
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.alertDialog
import com.goldmedal.hrapp.util.formatCurrency
import com.goldmedal.hrapp.util.snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class ChannelFinanceActivity : BaseActivity(), ApiStageListener<Any> {
    private lateinit var binding: ActivityChannelFinanceBinding
    private val viewModel: ChannelFinanceViewModel by viewModels()
    private var dealersList = arrayListOf <ChannelFinanceDealerItem>()
    private var userId: Int? = null
    private var cinNumber = ""
    private var totalLimit: Double = 0.0
    private var slNo: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelFinanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.apiListener = this
        observables()
        setClickListeners()
    }

    private fun observables() {
        viewModel.apply {
            getLoggedInUser().observe(this@ChannelFinanceActivity) { user ->
                if (user != null) {
                    userId = user.UserID
                    viewModel.getChannelFinanceDealers()
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            filterChannelFinanceDealer.setOnClickListener { showDealerDialog() }

            btnUpdateAmount.setOnClickListener {
                val inputAmount = etUpdateAmount.text.toString().trim()
                if (inputAmount.isEmpty()) {
                    alertDialog("Please enter amount to update")
                } else if (inputAmount.toDouble() > totalLimit) {
                    alertDialog("Amount should not be greater than total limit")
                } else {
                    viewModel.updateCFAmount(cinNumber, userId ?: 0, inputAmount, slNo ?: 0)
                }
            }
        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        Coroutines.main {
            binding.viewCommon.hide()
            if (callFrom == "channel_finance_dealers") {
                dealersList = _object as ArrayList<ChannelFinanceDealerItem>
                if (dealersList.isNotEmpty()) {
                    showDealerDialog()
                }
            } else if (callFrom == "channel_finance_dealer_wise_data") {
                val dealerWiseData = _object as ArrayList<DealerWiseData>
                if (dealerWiseData.isNotEmpty()) {
                    val dealerData = dealerWiseData[0]
                    slNo = dealerData.slno
                    totalLimit = dealerData.totallimit
                    binding.apply {
                        amtGroup.visibility = View.VISIBLE
                        tvAgingAmtValue.text = formatCurrency(dealerData.outstandingAmount)
                        tvAccountFrozenValue.text = dealerData.accountFrozen
                        tvTotalLimitValue.text = formatCurrency(dealerData.totallimit)
                        tvOutstandingValue.text = formatCurrency(dealerData.outstanding)
                        tvBalanceLimitValue.text = formatCurrency(dealerData.balancelimit)
                    }
                } else {
                    binding.amtGroup.visibility = View.VISIBLE
                    slNo = null
                }
            } else if (callFrom == "update_channel_finance_amount") {
                val data = _object as ArrayList<UpdateCNAmountItem>
                if (data.isNotEmpty()) {
                    val message = data[0].result
                    alertDialog(message)

                    binding.etUpdateAmount.setText("")
                    binding.etUpdateAmount.clearFocus()
                    viewModel.getChannelFinanceDealerWiseData(cinNumber)
                } else {
                    alertDialog("Something went wrong")
                }
            }
        }
    }

    override fun onError(
        message: String,
        callFrom: String,
        isNetworkError: Boolean,
    ) {
        binding.viewCommon.hide()
        if (callFrom == "channel_finance_dealer_wise_data") {
            binding.amtGroup.visibility = View.GONE
            slNo = null
        }

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

    private fun showDealerDialog() {
        val dialogBinding = DialogSearchableListBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(dialogBinding.root)
            .show()
        dialog.setCancelable(true)

        val adapter = object : BaseGenericRecyclerViewAdapter<ChannelFinanceDealerItem>(dealersList) {

            override fun setViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                val binding = RowSearchableListBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                return SearchableListViewHolder(binding)
            }

            override fun getViewType(position: Int): Int {
                return 0
            }

            override fun onBindData(holder: RecyclerView.ViewHolder?, data: ChannelFinanceDealerItem) {
                val viewHolder = holder as SearchableListViewHolder
                viewHolder.binding.tvRowSearchableList.text = data.dealnm

                viewHolder.itemView.setOnClickListener {
                    binding.filterChannelFinanceDealer.setText(data.dealnm, false)
                    cinNumber = data.cinnum
                    viewModel.getChannelFinanceDealerWiseData(cinNumber)
                    dialog.dismiss()
                }
            }
        }
        dialogBinding.rvDialogSearchableList.adapter = adapter

        dialogBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (query.toString().isNotEmpty()) {
                    val filteredList = dealersList.filter { it.dealnm.contains(query.toString(), true)
                            || it.cinnum.contains(query.toString(), true)
                    } as kotlin.collections.ArrayList<ChannelFinanceDealerItem>
                    adapter.addAllItems(filteredList)
                } else {
                    adapter.addAllItems(dealersList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}