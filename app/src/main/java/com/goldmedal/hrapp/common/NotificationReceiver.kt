package com.goldmedal.hrapp.common
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.network.GlobalConstant.NOTIFICATION_CHANNEL_ID

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Log or handle lack of permissions gracefully
            return
        }

        // Build and display the notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your icon
            .setContentTitle("Scheduled Notification")
            .setContentText("This is your notification after 9 hours!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(1001, notification) // Notification ID
        }
    }
}