package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.AttendanceDetailsData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName


data class AttendanceDetailsResponse(@SerializedName("Data")
                                     val attendanceDetailsData: List<AttendanceDetailsData>?,
                                     val StatusCode: String?,
                                     val StatusCodeMessage: String?,
                                     val Timestamp: String?,
                                     val Errors: List<ErrorData?>?)