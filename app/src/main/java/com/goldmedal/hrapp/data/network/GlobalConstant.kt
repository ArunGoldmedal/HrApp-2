package com.goldmedal.hrapp.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


object GlobalConstant {

    const val TYPE_NO_DATA = 10000
    const val BASE_URL = "https://api.goldmedalindia.in/api/"
    const val HRM_BASE_URL = "https://goldapi.goldmedalindia.in/api/hrm/v1.0/"
    const val SUCCESS_CODE = "200"
    const val NO_DATA_CODE = "2002"
    const val PUNCH_TYPE = "MOBILE"
    const val CLIENT_ID = "HRM_347362"
    const val CLIENT_SECRET = "8njmLe/g9ih+6wLxYx/O4D56N+1q7sR71CzZb4uJLhIeFQNiIzMnxm1kZAIUHyxtwM+CIkYw9ct7CCebDTIQPh9oyOBPz/bpdf+7oM6cU="
    const val APP_TYPE = "hrm"

   const val IMAGE_DIRECTORY = "/hrapp"

    const val ATTENDANCE_SUMMARY = "ATTENDANCE_SUMMARY"

    const val COMMON_IMAGE_UPLOAD_API = "common_image_upload"
    const val ADD_COMPANY_DETAILS_API = "add_company_details"
    const val GET_COMPANY_DETAILS_API = "get_company_details"
    const val DELETE_COMPANY_DETAILS_API = "delete_company_details"
    const val NOTIFICATION_CHANNEL_ID = "your_channel_id"
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "This is the default notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
