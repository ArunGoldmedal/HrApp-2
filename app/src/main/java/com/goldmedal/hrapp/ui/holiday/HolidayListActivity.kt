package com.goldmedal.hrapp.ui.holiday

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.data.db.entities.AllHolidayData
import com.goldmedal.hrapp.databinding.HolidayListActivityBinding
import com.goldmedal.hrapp.ui.dashboard.home.HomeViewModel
import com.goldmedal.hrapp.util.Coroutines
import com.goldmedal.hrapp.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.holiday_list_activity.*

@AndroidEntryPoint
class HolidayListActivity : AppCompatActivity(), ApiStageListener<Any> {



private val holidayModel: HomeViewModel by viewModels()

    private lateinit var holidayActivityBinding : HolidayListActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        holidayActivityBinding = DataBindingUtil.setContentView(this, R.layout.holiday_list_activity)
        holidayActivityBinding.viewmodel = holidayModel



        holidayModel.apiListener = this


            holidayModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null) {
                    holidayModel.allHolidays(user.UserID)
                }
            })

    }


    private fun List<AllHolidayData?>.toHolidayData(): List<HolidayListItem?> {
        return this.map {
            HolidayListItem(it)
        }
    }


    private fun bindUI(list: List<AllHolidayData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toHolidayData())
        }
    }

    private fun initRecyclerView(toHolidayData: List<HolidayListItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toHolidayData)
        }

        rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        bindUI(_object as List<AllHolidayData?>)
        progress_bar?.stop()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        root_layout?.snackbar(message)


        holidayModel.getAllHolidayDataDetail().observe(this, Observer {
            if (it.isNotEmpty()) {
                val holidayData = it
                Log.d("all holiday list ", "" + it.size)
                bindUI(holidayData)

            }
        })





    }

    override fun onValidationError(message: String, callFrom: String) {

    }


}
