package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goldmedal.hrapp.data.network.GlobalConstant

@Entity
data class HolidayData(

        val HolidayID: Int? = null,
        val HolidayFromDate: String? = null,
        val HolidayToDate: String? = null,
        val HolidayName: String? = null,
        val FromDateDay: String? = null,
        val ToDateDay: String? = null,
        val FromDateFormat: String? = null,
        val ToDateFormat: String? = null,
        var ViewType: Int? = GlobalConstant.TYPE_NO_DATA,
        val Description: String? = null

) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0


    constructor() : this(-1,"", "",

            "", "", "", "", "", 1001,
            ""
    )
}