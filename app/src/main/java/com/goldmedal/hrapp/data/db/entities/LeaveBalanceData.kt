package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LeaveBalanceData(


        val TotalLeaves: Double? = 0.0,
        val LeavesTaken: Double? = 0.0,
        val PendingLeaves: Double? = 0.0,
        val AvailableLeaves: Double? = 0.0

) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}