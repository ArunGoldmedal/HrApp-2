package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class EmployeeAttendanceData(


        val TotalEmployee: Int? = 0,


        @SerializedName("PrsentEmployee")
        val PresentCount: Int? = 0,

        @SerializedName("AbsentEmployee")
        val AbsentCount: Int? = 0
)

{
        @PrimaryKey(autoGenerate = true)
        var uid: Int = 0

}