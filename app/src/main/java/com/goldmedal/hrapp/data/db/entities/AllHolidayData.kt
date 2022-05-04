package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class AllHolidayData(

        val HolidayID: Int? = null,
        val HolidayFromDate: String? = null,
        val HolidayToDate: String? = null,
        val HolidayName: String? = null,
        val FromDateDay: String? = null,
        val ToDateDay: String? = null,
        val FromDateFormat: String? = null,
        val ToDateFormat: String? = null,
        val Description: String? = null

){
        @PrimaryKey(autoGenerate = true)
        var uid: Int = 0

}