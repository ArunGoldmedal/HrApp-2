package com.goldmedal.hrapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName


@Entity
public class UserDataUpdate {
    @ColumnInfo(name = "uid")
    val uid: Int = CURRENT_USER_ID

    @ColumnInfo(name = "HomeAddress")
    @SerializedName("HomeAddress")
     val HomeAddress: String? = null

    @SerializedName("MobileNo")
    @ColumnInfo(name = "MobileNumber")
     val MobileNo: String? = null

    @SerializedName("FatherName")
    @ColumnInfo(name = "FatherName")
    val FatherName: String? = null


    @SerializedName("MotherName")
    @ColumnInfo(name = "MotherName")
    val MotherName: String? = null

    @SerializedName("SpouseName")
    @ColumnInfo(name = "SpouseName")
    val SpouseName: String? = null


    @SerializedName("AnniversaryDOB")
    @ColumnInfo(name = "AnniversaryDOB")
    val AnniversaryDOB: String? = null


    @SerializedName("FatherDOB")
    @ColumnInfo(name = "FatherDOB")
    val FatherDOB: String? = null

    @SerializedName("MotherDOB")
    @ColumnInfo(name = "MotherDOB")
    val MotherDOB: String? = null


    @SerializedName("MartialStatus")
    @ColumnInfo(name = "MaritalID")
    val MartialStatus: String? = null

    @SerializedName("SpouseDOB")
    @ColumnInfo(name = "SpouseDOB")
    val SpouseDOB: String? = null


    @SerializedName("PersonalEmail")
    @ColumnInfo(name = "PersonalEmail")
    val PersonalEmail: String? = null

    @SerializedName("OfficeEmail")
    @ColumnInfo(name = "Officialemail")
    val OfficeEmail: String? = null



    @SerializedName("ChildName1")
    @ColumnInfo(name = "ChildName1")
    val ChildName1: String? = null

    @SerializedName("ChildName2")
    @ColumnInfo(name = "ChildName2")
    val ChildName2: String? = null

    @SerializedName("ChildName3")
    @ColumnInfo(name = "ChildName3")
    val ChildName3: String? = null


    @SerializedName("ChildDOB1")
    @ColumnInfo(name = "ChildDOB1")
    val ChildDOB1: String? = null

    @SerializedName("ChildDOB2")
    @ColumnInfo(name = "ChildDOB2")
    val ChildDOB2: String? = null

    @SerializedName("ChildDOB3")
    @ColumnInfo(name = "ChildDOB3")
    val ChildDOB3: String? = null
}