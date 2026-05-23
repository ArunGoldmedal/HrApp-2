package com.goldmedal.hrapp.data.model

data class GetCompanyDetailsData(
    val AppType: String,
    val CINNo: String,
    val Category: String,
    val CompanyAddress: String,
    val CompanyDetailsID: Int,
    val CompanyName: String,
    val CreateDate: String,
    val ModifyDate: String,
    val ProductImages: String,
    val Remark: String,
    val UserID: Int,
    val VisitingCardImages: String
)