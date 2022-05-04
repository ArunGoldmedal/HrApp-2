package com.goldmedal.hrapp.ui.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldmedal.hrapp.common.ZoomOutPageTransformer
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.databinding.ActivityTeamProfileHistoryBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

private const val NUM_PAGES = 2
@AndroidEntryPoint
class TeamProfileHistoryActivity : AppCompatActivity(){
    private var modelItem: MyTeamData? = null
    private lateinit var binding: ActivityTeamProfileHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTeamProfileHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM_ITEM)
            if (modelItem != null) {
                supportActionBar?.title = modelItem?.EmployeeName
            }
        }
        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager
        ) { tab, position ->
            tab.text = when (position) {
                0 -> "Profile"
                else -> "History"
            }
        }.attach()
    }
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    TeamProfileFragment.newInstance(modelItem)
                }
                else -> {
                    TeamLeaveHistoryFragment.newInstance(modelItem)
                }
            }
        }
    }

    companion object {
        private const val ARG_PARAM_ITEM = "model_item"
        fun start(context: Context, modelItem: MyTeamData?) {
            val intent = Intent(context, TeamProfileHistoryActivity::class.java)
            intent.putExtra(ARG_PARAM_ITEM, modelItem)
            context.startActivity(intent)

        }
    }
}