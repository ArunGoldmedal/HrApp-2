package com.goldmedal.hrapp.common

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.ui.dashboard.DashboardActivity

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Check for notification permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Log the lack of permission and return failure
                return Result.failure()
            }
        }

        // Intent to open the app when the notification is clicked
        val intent = Intent(applicationContext, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent for the notification
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create and show the notification
        val notification = NotificationCompat.Builder(applicationContext, "your_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Punch Out Reminder")
            .setContentText("It looks like you forgot to Punch Out.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Attach PendingIntent
            .setAutoCancel(true) // Dismiss notification when clicked
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(1001, notification) // Notification ID

        return Result.success()
    }
}
