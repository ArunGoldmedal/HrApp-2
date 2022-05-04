package com.goldmedal.hrapp.ui.accounts

//import kotlinx.android.synthetic.main.activity_login.progress_bar

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.IncreaseLimitPartyData
import com.goldmedal.hrapp.data.model.AgingDetail
import com.goldmedal.hrapp.data.model.LimitPartyDetailData
import com.goldmedal.hrapp.databinding.ActivityAccountsDetailBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.hide
import com.goldmedal.hrapp.util.show
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com. xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_accounts_detail.*

@AndroidEntryPoint
class AccountsDetailActivity : AppCompatActivity(), DetailListener {


//    private val factory: AccountsViewModelFactory by instance()

    private  val limitData: AccountsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAccountsDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounts_detail)

//        limitData = ViewModelProviders.of(this, factory).get(AccountsViewModel::class.java)
        binding.viewmodel = limitData

        limitData.getAccountDetailData()
        limitData.getAgingData()
        limitData.detailListener = this
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
        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onSuccess(partyList: List<IncreaseLimitPartyData?>?, agingList: List<AgingDetail?>?, partyDetailList: List<LimitPartyDetailData?>?){
        bindUI(agingList)
        initSpinner(partyList)
        progress_bar.hide()
        print("Party List - - - " + partyList)
        print("Aging List - - - " + agingList)
    }

    private fun initSpinner(partyList: List<IncreaseLimitPartyData?>?) {
        var spLimitParty: SmartMaterialSpinner<*>? = null
        var listLimitParty: List<String>? = null
        spLimitParty = findViewById(R.id.sp_searchable)
        listLimitParty = ArrayList()
        partyList.let {
            for (i in 1..((it?.size) ?: 0)) {
//      println(i)
                listLimitParty.add((it?.get(i-1)?.displaynm ?: "NO ITEM"))
            }
             spLimitParty.item = listLimitParty
             spLimitParty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                    Toast.makeText(this@AccountsDetailActivity, listLimitParty[position], Toast.LENGTH_SHORT).show()

                }
                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }
        }
    }


    override fun onFailure(message: String, reason: String) {
        progress_bar?.hide()
        root_layout?.snackbar(message)
    }

}
