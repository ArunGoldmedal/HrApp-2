package com.goldmedal.hrapp.ui.dashboard

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.databinding.ActivityDashboardBinding
import com.goldmedal.hrapp.databinding.NavHeaderHomeScreenBinding
import com.goldmedal.hrapp.inappupdates.UpdateManager
import com.goldmedal.hrapp.inappupdates.UpdateManagerConstant
import com.goldmedal.hrapp.ui.auth.*
import com.goldmedal.hrapp.ui.dashboard.notification.NotificationActivity
import com.goldmedal.hrapp.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(),  UpdateAppDialogFragment.OnCancelUpdate {
    private  val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mHeaderBinding: NavHeaderHomeScreenBinding
    private var mToast: Toast? = null

    private var forceUpdate: Boolean = false
    private var playStoreVersionCode: Int = 0

    private var wasFlexibleUpdatedCancel: Boolean = false

    // Declare the UpdateManager
    private var mUpdateManager: UpdateManager? = null

    private lateinit var navController: NavController
    companion object {


        private const val TAG = "DashboardActivity"
        private const val MEGABYTES = 1024L * 1024L

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.viewmodel = viewModel
        mHeaderBinding = NavHeaderHomeScreenBinding.bind(binding.navigationView.getHeaderView(0))

        setSupportActionBar(binding.appBarHomeScreen.appBarToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        mToast = Toast.makeText(this@DashboardActivity, R.string.press_back_again, Toast.LENGTH_SHORT)

        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarHomeScreen.appBarToolbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.appBarHomeScreen.bottomNavContent.bottomNav.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)


        val logoutItem = binding.navigationView.menu.findItem(R.id.actionLogout)
        logoutItem.setOnMenuItemClickListener {
            logout()
            true
        }

        binding.appBarHomeScreen.appBarToolbar.toolbarNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        playStoreVersionCode = viewModel.getVersionCode() ?: 1
        forceUpdate = viewModel.getForceUpdateFlag()


        // Initialize the Update Manager with the Activity and the Update Mode
        mUpdateManager = UpdateManager.Builder(this)

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {

                mHeaderBinding.textViewMsg.text = "Hello, " + user.FirstName

                val avatar = if (user.Genderid.equals("1")) R.drawable.male_avatar else R.drawable.female_avatar


                Glide.with(this)
                        .load(user.ProfilePicture)
                        .fitCenter()
                        .placeholder(avatar)
                        .into(mHeaderBinding.imageViewProfile)
                if (user.IsReportingPerson == 1) {
                    setTitle("MANAGER")
                }
                if (user.ISHr == 1) {
                    setTitle("HR")
                }

                if (user.ShowLimitDetails == false) {
                    hideLimitDetails()
                }

                if (user.IsReportingPerson == 0 && user.ISHr == 0) {
                    hideAdminOptions()
                }


            }
        })

        mUpdateManager?.addUpdateInfoListener(object : UpdateManager.UpdateInfoListener {
            override fun onReceiveVersionCode(code: Int) {
                Log.d(TAG, "onReceiveVersionCode: " + code)
            }

            override fun onReceiveStalenessDays(days: Int) {
                Log.d(TAG, "onReceiveStalenessDays: "+days)
            }
        })
// Callback from Flexible Update Progress
        // This is only available for Flexible mode
        mUpdateManager?.addFlexibleUpdateDownloadListener(object : UpdateManager.FlexibleUpdateDownloadListener {
            override fun onDownloadProgress(bytesDownloaded: Long, totalBytes: Long) {

                val MbDownloaded = bytesDownloaded / MEGABYTES
                val totalMbToDownload = totalBytes / MEGABYTES

                getDownloadProgress(MbDownloaded,totalMbToDownload)
            }

            override fun onReceiveAppUpdate() {
                Log.d(TAG, "crossCheckPlayStoreVersion:" )
                crossCheckPlayStoreVersion()
            }
        })
    }



    override fun onResume() {
        super.onResume()
        // Check App Update here
        if(!wasFlexibleUpdatedCancel){
            checkForAppUpdate()
        }

    }




    private fun logout() {
        viewModel.logoutUser()
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
        finish()
    }

/* Calculates Download Progress in Percentage*/
    private fun getDownloadProgress(MbDownloaded: Long, totalMbToDownload: Long){
        shortToast ("downloaded: ${(MbDownloaded * 100 / totalMbToDownload)}% $MbDownloaded MB/$totalMbToDownload MB")
    }

    private fun setTitle(message: String) {
        val navMenu = binding.navigationView.menu
        navMenu.findItem(R.id.headerTitle).title = message

    }


    /*
    * When employee is not manager or HR hide these options
    */
    private fun hideAdminOptions() {
        val navMenu = binding.navigationView.menu
        navMenu.findItem(R.id.teamRequestsArchiveActivity).isVisible = false
        navMenu.findItem(R.id.requestsFragment).isVisible = false
        navMenu.findItem(R.id.myTeamActivity).isVisible = false
        navMenu.findItem(R.id.odRequestsActivity).isVisible = false
        navMenu.findItem(R.id.slRequestsActivity).isVisible = false
        navMenu.findItem(R.id.regularizationRequestsActivity).isVisible = false
        navMenu.findItem(R.id.teamRegularizationHistoryActivity).isVisible = false

        //Hide Leave Requests on Bottom Navigation
        binding.appBarHomeScreen.bottomNavContent.bottomNav.menu.removeItem(R.id.requestsFragment)
    }


    fun showRequestsFragment(){
        navController.navigate(R.id.requestsFragment)
    }

    private fun hideLimitDetails() {
        val navMenu = binding.navigationView.menu
        navMenu.findItem(R.id.accountsDetailActivity).isVisible = false
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                if (mToast?.view?.isShown == false) {
                    mToast?.show()
                    return
                } else {
                    //Exit App
                    super.onBackPressed()
                }

            }
            //Move back to home
            super.onBackPressed()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    //In-App Update Flow - - - - - - - - - - - - - - - - - - - - - -
    private fun checkForAppUpdate() {
        // Initialize the Update Manager with the Activity and the Update Mode
        mUpdateManager = UpdateManager.Builder(this)

        if (forceUpdate) {
            mUpdateManager?.mode(UpdateManagerConstant.IMMEDIATE)?.start()
        }else{
            mUpdateManager?.mode(UpdateManagerConstant.FLEXIBLE)?.start()
        }
    }

    private fun crossCheckPlayStoreVersion() {
        val currentVersion: Int = getVersionCode()
        if (playStoreVersionCode > currentVersion) {
            val updateAppDialog = UpdateAppDialogFragment.newInstance(forceUpdate, this)
            updateAppDialog.show(supportFragmentManager, UpdateAppDialogFragment.TAG)
        } else {

        }
    }

    private fun getVersionCode(): Int {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
        return longVersionCode.toInt()

    }



    override fun pressedNoThanks() {}

    override fun pressedUpdate() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UpdateManagerConstant.REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                if (forceUpdate) {
                    checkForAppUpdate()
                }
                Log.d(TAG, "Here Update flow failed! Result code: $resultCode")
            }
            if (!forceUpdate) {
                wasFlexibleUpdatedCancel = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
