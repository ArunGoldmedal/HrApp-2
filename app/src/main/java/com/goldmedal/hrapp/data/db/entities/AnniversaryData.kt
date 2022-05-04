package com.goldmedal.hrapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goldmedal.hrapp.data.network.GlobalConstant

@Entity
data class AnniversaryData(
//        val EmployeeName: String? = null,
//        val Anniversarydate: String? = null

        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "employee_name") val
        EmployeeName: String?,
        @ColumnInfo(name = "anniversary_date") val Anniversarydate: String?,
        val ProfilePicture: String? = null,
        val Gender: String? = null,
        var ViewType: Int =GlobalConstant.TYPE_NO_DATA ,
        var Message: String? = null
) {
    constructor() : this(0, "",

            "", "", "",
            1001, ""
    )
}