package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.db.entities.GetAllAttendanceData
import com.goldmedal.hrapp.data.model.ErrorData
import com.google.gson.annotations.SerializedName

data class GetAllAttendanceResponse(@SerializedName("Data")
                                        val attendanceData: List<GetAllAttendanceData>?,

                                    @SerializedName("StatusCodeMessage")
                                        val StatusCodeMessage: String?,

                                    @SerializedName("Timestamp")
                                        val servertime: String?,

                                    val Errors: List<ErrorData?>?)



