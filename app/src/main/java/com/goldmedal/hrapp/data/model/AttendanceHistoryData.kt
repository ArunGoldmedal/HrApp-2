package com.goldmedal.hrapp.data.model

import java.io.Serializable


data class AttendanceHistoryData(
        val UserID: Int? = null,
        val Username: String? = null,
        val Email: String? = null,
        val contactno: String? = null,
        val FirstIn: String? = null,
        val LastOut: String? = null,
        val TotalHours: String? = null,
        val GenderId: Int? = null,
        val ProfilePhoto: String? = null,
        val Designation: String? = null
) : Serializable

