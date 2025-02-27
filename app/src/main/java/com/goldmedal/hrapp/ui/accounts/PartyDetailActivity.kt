package com.goldmedal.hrapp.ui.accounts

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.db.entities.IncreaseLimitPartyData
import com.goldmedal.hrapp.data.model.AgingDetail
import com.goldmedal.hrapp.data.model.LimitPartyDetailData
import com.goldmedal.hrapp.databinding.ActivityPartyDetailBinding
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PartyDetailActivity : AppCompatActivity(), DetailListener {



//    private val factory: AccountsViewModelFactory by instance()

    private  val limitDataParty: AccountsViewModel by viewModels()
    private lateinit var binding: ActivityPartyDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_party_detail)
//        limitDataParty = ViewModelProviders.of(this, factory).get(AccountsViewModel::class.java)
        binding.viewmodelparty = limitDataParty

        limitDataParty.getPartyDetailData()

        limitDataParty.detailListener = this
    }


    private fun List<LimitPartyDetailData?>.toLimitDetailParty(): List<PartyDetailItem?> {
        return this.map {
            PartyDetailItem(it)
        }
    }


    private fun bindUI(list: List<LimitPartyDetailData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toLimitDetailParty())
        }


    }

    private fun initRecyclerView(toLimitDetailParty: List<PartyDetailItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            print("Count - - -" + toLimitDetailParty.size)
            binding.rootLayout.snackbar("Count - - -" + toLimitDetailParty.size)

            addAll(toLimitDetailParty)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }


        initSearchView()
    }

    private fun initSearchView() {

        var searchView = findViewById<SearchView>(R.id.searchLimit)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                limitDataParty.strSearchText = newText

                if (newText.trim { it <= ' ' }.length == 0 || newText.trim { it <= ' ' }.length > 2) {
                    limitDataParty.getPartyDetailData()
                }

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
    }


    override fun onStarted() {
        // progress_bar.show()
        binding.viewCommon.showProgressBar()
    }


    override fun onSuccess(
        limitIncreaseMessage: String?,
        partyList: List<IncreaseLimitPartyData?>?,
        agingList: List<AgingDetail?>?,
        partyDetailList: List<LimitPartyDetailData?>?
    ) {
        bindUI(partyDetailList)

        // progress_bar.hide()
        if (partyDetailList.isNullOrEmpty()) {
            binding.viewCommon.showNoData()
        } else {
            binding.viewCommon.hide()
        }

        binding.rootLayout.snackbar("Party detail List - - - " + partyDetailList?.size)
    }


    override fun onFailure(message: String, reason: String) {
        // progress_bar.hide()
        if(reason == "net"){
            binding.viewCommon.showNoInternet()
        }else{
            binding.viewCommon.showServerError()
        }

        binding.rootLayout.snackbar(message)
    }

}
