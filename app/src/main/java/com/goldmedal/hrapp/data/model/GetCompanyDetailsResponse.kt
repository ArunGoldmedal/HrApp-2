package com.goldmedal.hrapp.data.model

data class GetCompanyDetailsResponse(
    val Data: List<GetCompanyDetailsData>?,
    val Errors: List<ErrorData>?,
    val Size: Int,
    val StatusCode: Int,
    val StatusCodeMessage: String,
    val Timestamp: String,
    val Version: String
)
