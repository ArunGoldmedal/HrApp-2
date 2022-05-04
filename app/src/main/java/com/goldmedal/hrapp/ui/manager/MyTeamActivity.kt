package com.goldmedal.hrapp.ui.manager

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.databinding.ActivityMyTeamBinding
import com.goldmedal.hrapp.ui.dashboard.home.HomeViewModel
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MyTeamActivity : AppCompatActivity(), ApiStageListener<Any> {


private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: ActivityMyTeamBinding
    private var filteredList: List<MyTeamData?> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.apiListener = this
        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getMyTeam(user.UserID)
            }
        })

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                filter(charSequence.toString())
            }
            override fun afterTextChanged(editable: Editable) {}
        })

    }

    private fun filter(text: String) {
Coroutines.io {
    val filteredNames = filteredList.filter {
        (it?.EmployeeName?.toLowerCase(Locale.getDefault())?.contains(text.toLowerCase(Locale.getDefault())) == true)
    }
    filterUI(filteredNames)
}
}
    private fun filterList(data: List<MyTeamItem?>) = Coroutines.main {
    val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
        addAll(data)
    }

    binding.rvList.apply {
        adapter = mAdapter
    }


    if(data.isEmpty()){
        binding.viewCommon.showNoData()
    }else{
        binding.viewCommon.hide()
    }
    }

    //RecyclerView Data source
    private fun List<MyTeamData?>.toData(): List<MyTeamItem?> {
        return this.map {
            MyTeamItem(it, this@MyTeamActivity)
        }
    }

    private fun initRecyclerView(data: List<MyTeamItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@MyTeamActivity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<MyTeamData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toData())
        }
    }

    private fun filterUI(list: List<MyTeamData?>?) = Coroutines.main {
        list?.let {
            filterList(it.toData())
        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()

        filteredList = _object as List<MyTeamData?>

        bindUI(filteredList)

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        binding.viewCommon.hide()
        viewModel.getMyTeamData().observe(this, {
            if (it != null) {
                bindUI(it)
            } else {
                if (isNetworkError) {
                    binding.viewCommon.showNoInternet()
                } else {
                    binding.viewCommon.showNoData()
                }
                binding.rootLayout.snackbar(message)
            }

        })
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }
}