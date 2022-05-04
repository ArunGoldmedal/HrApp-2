package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "GetAllAttendanceData", indices = [Index(value = ["DisplayDate"], unique = true)])
@Entity
data class GetAllAttendanceData
(
        val DisplayDate: String? = null,
        val FirstIn: String? = null,
        val LastOut: String? = null,
        val TotalHours: String? = null,
//        val PunchType: String? = null,
        val HolidayName: String? = null,
        val FirstInLocation: String? = null,
        val LastOutLocation: String? = null,
        val FirstInLatitude: String? = null,
        val FirstInLongitude: String? = null,
        val FirstInPunchType: String? = null,
        val LastOutPunchType: String? = null,
        val LastOutLatitude: String? = null,
        val LastOutLongitude: String? = null,
//        val PunchIn_PunchOut: String? = null,
        val status: String? = null
//        val Type: String? = null
)
{
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}
