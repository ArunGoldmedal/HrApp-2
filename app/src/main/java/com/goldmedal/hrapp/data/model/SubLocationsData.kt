package com.goldmedal.hrapp.data.model

data class SubLocationsData(
    val SublocationID: Int?,
    val SublocationName: String?,
    val Lat: String?,
    var SubLocationAddress: String? = "-",
    val Long: String?
)