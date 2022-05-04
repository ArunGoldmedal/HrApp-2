package com.goldmedal.hrapp.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class MyTeamData(
        val Slno: Int?,
        val EmployeeName: String?,
        val ProfileImg: String?
) : Parcelable  {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}