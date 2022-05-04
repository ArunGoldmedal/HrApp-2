package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.GetCurrentAttendanceData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class GetCurrentAttendanceResponse(@SerializedName("Data")
                                        val currAttendanceData: List<GetCurrentAttendanceData>?,

                                        @SerializedName("StatusCodeMessage")
                                        val StatusCodeMessage: String?,

                                        @SerializedName("Timestamp")
                                        val servertime: String?,

                                        val Errors: List<ErrorData?>?)



