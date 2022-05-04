package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goldmedal.hrapp.data.network.GlobalConstant


@Entity
data class BirthdayData(

        val EmployeeName: String? = null,
        val Birthdate: String? = null,
        val ProfilePicture: String? = null,
        val Gender: String? = null,
        var ViewType: Int = GlobalConstant.TYPE_NO_DATA,
        var Message: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0


    constructor() : this("", "",

            "", "", 1001,
            ""
    )

}