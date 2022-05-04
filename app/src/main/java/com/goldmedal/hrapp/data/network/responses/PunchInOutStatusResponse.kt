package com.goldmedal.hrapp.data.network.responses

import com.goldmedal.hrapp.data.model.ErrorData
import com.goldmedal.hrapp.data.model.PunchInOutStatusData
import com.google.gson.annotations.SerializedName

data class PunchInOutStatusResponse(@SerializedName("Data")
                                    val punchStatusData: List<PunchInOutStatusData>?,

                                    val StatusCode: String?,

                                    val StatusCodeMessage: String?,

                                    @SerializedName("Timestamp")
                                    val serverTime: String?,

                                    val Errors: List<ErrorData?>?)



