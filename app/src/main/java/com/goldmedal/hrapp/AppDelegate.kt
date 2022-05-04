package com.goldmedal.hrapp


import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by goldmedal on 22/08/18.
 */
@HiltAndroidApp
class AppDelegate : Application() {


    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}
