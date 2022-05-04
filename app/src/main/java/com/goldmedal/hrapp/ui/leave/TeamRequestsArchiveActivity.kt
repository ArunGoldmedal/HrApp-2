package com.goldmedal.hrapp.ui.leave


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldmedal.hrapp.common.ApiStageListener
import com.goldmedal.hrapp.common.ZoomOutPageTransformer
import com.goldmedal.hrapp.data.model.LeaveRequestsData
import com.goldmedal.hrapp.databinding.ActivityTeamRequestsArchiveBinding
import com.goldmedal.hrapp.util.getDateFromString
import com.goldmedal.hrapp.util.snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


private const val NUM_PAGES = 3

@AndroidEntryPoint
class TeamRequestsArchiveActivity : AppCompatActivity(), ApiStageListener<Any>{





    private val leaveModel: LeaveViewModel by viewModels()


    private lateinit var binding: ActivityTeamRequestsArchiveBinding

    private var futureApprovedArray: List<LeaveRequestsData?> = emptyList()
    private var pastApprovedArray: List<LeaveRequestsData?> = emptyList()
    private var rejectedArray: List<LeaveRequestsData?> = emptyList()

    private var isError: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamRequestsArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        leaveModel.apiListener = this


        leaveModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                leaveModel.getLeaveRequests(user.UserID, 2)
            }

        })

        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager,
                object : TabLayoutMediator.TabConfigurationStrategy {
                    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                        tab.text = when (position) {
                            0 -> "Future Approved"
                            1 -> "Past Approved"
                            else -> "Rejected"

                        }
                    }
                }).attach()
    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    RequestsArchiveFragment.newInstance(futureApprovedArray.toMutableList(), isError)
                }
                1 -> {
                    RequestsArchiveFragment.newInstance(pastApprovedArray.toMutableList(), isError)
                }
                else -> {
                    RequestsArchiveFragment.newInstance(rejectedArray.toMutableList(), isError)
                }
            }
        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        val data = _object as List<LeaveRequestsData?>


        futureApprovedArray = data.filter {
            !it?.Status.equals("Rejected", ignoreCase = true) && Date().before(getDateFromString(it?.ToDate
                    ?: "01-01-1900", "dd/MM/yyyy", Locale.getDefault()))} //|| it?.Status.equals("Partially Approved", ignoreCase = true)

        pastApprovedArray = data.filter {
            !it?.Status.equals("Rejected", ignoreCase = true) && Date().after(getDateFromString(it?.ToDate
                    ?: "01-01-1900", "dd/MM/yyyy", Locale.getDefault()))}

        rejectedArray = data.filter { it?.Status.equals("Rejected", ignoreCase = true) }

        if (data.isNullOrEmpty()) {
            isError = true
            binding.viewCommon.showNoDataImage()
        } else {
            isError = false
            binding.viewCommon.hide()


        }


        setPager()
    }

    private fun setPager() {

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        if (!futureApprovedArray.isNullOrEmpty()) {
            binding.tabs.getTabAt(0)?.orCreateBadge?.number = futureApprovedArray.size
            binding.tabs.getTabAt(0)?.badge?.horizontalOffset = 0
            binding.tabs.getTabAt(0)?.badge?.verticalOffset = 0
        }

        if (!pastApprovedArray.isNullOrEmpty()) {
            binding.tabs.getTabAt(1)?.orCreateBadge?.number = pastApprovedArray.size
            binding.tabs.getTabAt(1)?.badge?.horizontalOffset = 0
            binding.tabs.getTabAt(1)?.badge?.verticalOffset = 0
        }

        if (!rejectedArray.isNullOrEmpty()) {
            binding.tabs.getTabAt(2)?.orCreateBadge?.number = rejectedArray.size
            binding.tabs.getTabAt(2)?.badge?.horizontalOffset = 0
            binding.tabs.getTabAt(2)?.badge?.verticalOffset = -10
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showNoDataImage()
        }

        binding.rootLayout.snackbar(message)

        isError = true

        setPager()
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }
}