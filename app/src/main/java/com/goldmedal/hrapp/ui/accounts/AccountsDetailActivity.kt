package com.goldmedal.hrapp.ui.accounts

//import kotlinx.android.synthetic.main.activity_login.progress_bar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.goldmedal.hrapp.BaseActivity
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.IncreaseLimitPartyData
import com.goldmedal.hrapp.data.model.AgingDetail
import com.goldmedal.hrapp.data.model.LimitPartyDetailData
import com.goldmedal.hrapp.databinding.ActivityAccountsDetailBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.hide
import com.goldmedal.hrapp.util.show
import com.goldmedal.hrapp.util.snackbar
import com.goldmedal.hrapp.util.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsDetailActivity : BaseActivity(), DetailListener {


//    private val factory: AccountsViewModelFactory by instance()

    private  val limitData: AccountsViewModel by viewModels()
    private lateinit var binding: ActivityAccountsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_accounts_detail)

//        limitData = ViewModelProviders.of(this, factory).get(AccountsViewModel::class.java)
        binding.viewmodel = limitData

        limitData.getAccountDetailData()
        //limitData.getAgingData()
        limitData.detailListener = this

        binding.btnUpdateLimit.setOnClickListener {
            if (limitData.strPartyCin == null) {
                toast("Please select a party")
            } else if (binding.etAmount.text.toString().isEmpty()) {
                toast("Please enter amount")
            } else {
                limitData.updateAmount = binding.etAmount.text.toString()
                limitData.getLoggedInUser().observe(this) { user ->
                    if (user != null) {
                        user.UserID?.let { userId ->
                            limitData.updateLimit(
                                limitData.strPartyCin!!,
                                limitData.updateAmount!!,
                                userId
                            )
                        }
                    }
                }
            }
        }
    }


    private fun List<AgingDetail?>.toAgingParty(): List<AgingItem?> {
        return this.map {
            AgingItem(it)
        }
    }


    private fun bindUI(list: List<AgingDetail?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toAgingParty())
        }
    }

    private fun initRecyclerView(toAgingParty: List<AgingItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAgingParty)
        }
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess(
        limitIncreaseMessage: String?,
        partyList: List<IncreaseLimitPartyData?>?,
        agingList: List<AgingDetail?>?,
        partyDetailList: List<LimitPartyDetailData?>?
    ) {
        agingList?.let {
            binding.llAgingHeader.visibility = View.VISIBLE
            bindUI(it)
        }

        limitIncreaseMessage?.let {
            limitData.updateAmount = null
            binding.etAmount.setText("")
            binding.etAmount.clearFocus()
            binding.rootLayout.snackbar(it)
        }

        partyList?.let {
            initSpinner(it)
        }

        binding.progressBar.hide()
        Log.d("TAG", " Party List - ${partyList.toString()}")
        Log.d("TAG", " Aging List - ${agingList.toString()}")
    }

    private fun initSpinner(partyList: List<IncreaseLimitPartyData?>?) {
        var spLimitParty: SmartMaterialSpinner<String>? = null
        var listLimitParty: List<String>? = null
        spLimitParty = findViewById(R.id.sp_searchable)
        listLimitParty = ArrayList()
        partyList.let {
            for (i in 1..((it?.size) ?: 0)) {
                listLimitParty.add((it?.get(i-1)?.displaynm ?: ""))
            }
             spLimitParty.item = listLimitParty
             spLimitParty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                    //Toast.makeText(this@AccountsDetailActivity, listLimitParty[position], Toast.LENGTH_SHORT).show()
                     for (party in partyList!!) {
                         if (party?.displaynm == listLimitParty[position]) {
                             binding.llAgingHeader.visibility = View.GONE
                             bindUI(emptyList())
                             limitData.strPartyCin = party.cin
                             limitData.getAgingData()
                         }
                     }

                }
                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
        }
    }


    override fun onFailure(message: String, reason: String) {
        binding.progressBar.hide()
        binding.rootLayout.snackbar(message)
    }

}
