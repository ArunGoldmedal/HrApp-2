package com.goldmedal.hrapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LeaveTypeData(
        val LeaveTypeID: String?,
        val LeaveTypeName: String? ,
        val LeaveCount: String?,
        val LeaveTypeColour: String?
): Parcelable