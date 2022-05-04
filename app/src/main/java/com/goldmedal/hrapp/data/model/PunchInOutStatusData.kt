package com.goldmedal.hrapp.data.model

import com.google.gson.annotations.SerializedName

data class PunchInOutStatusData(
        val Status: Boolean? = null,
        val OfficeLat: String? = null,
        val OfficeLong: String? = null,
        val PunchIn: String? = null,
        val LastCheckIn: String? = null,
        val Address: String? = null,
        val ODSublocation: String? = null,


        @SerializedName("IsAtteandanceValid")
        val IsAttendanceValid: Boolean? = null,


        val IsGeoFenceLock: Boolean? = null
)