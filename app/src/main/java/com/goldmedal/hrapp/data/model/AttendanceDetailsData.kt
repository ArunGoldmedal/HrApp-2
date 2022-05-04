package com.goldmedal.hrapp.data.model

data class AttendanceDetailsData(
        val DisplayDate: String? = null,
        val FirstIn: String? = null,
        val LastOut: String? = null,
        val TotalHours: String? = null,
        val HolidayName: String? = null,
        val FirstInLocation: String? = null,
        val LastOutLocation: String? = null,
        val FirstInLatitude: String? = null,
        val FirstInLongitude: String? = null,
        val FirstInPunchType: String? = null,
        val LastOutPunchType: String? = null,
        val LastOutLatitude: String? = null,
        val LastOutLongitude: String? = null,
        val status: String? = null
)