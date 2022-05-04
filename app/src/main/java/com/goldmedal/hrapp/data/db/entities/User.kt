package com.goldmedal.hrapp.data.db.entities


import androidx.room.Entity
import androidx.room.PrimaryKey


const val CURRENT_USER_ID = 1

@Entity
data class User(
        val AnniversaryDOB: String? = null,
        val BranchName: String? = null,
        val ChildDOB1: String? = null,
        val ChildDOB2: String? = null,
        val ChildDOB3: String? = null,
        val ChildName1: String? = null,
        val ChildName2: String? = null,
        val ChildName3: String? = null,
        val DateOfBirth: String? = null,
        val Department: String? = null,
        val Designation: String? = null,
        val EmployeeCode: String? = null,
        val EmployeeFullName: String? = null,
        val EmployeeID: String? = null,
        val FatherDOB: String? = null,
        val FatherName: String? = null,
        val FirstName: String? = null,
        val Gender: String? = null,
        val Genderid: String? = null,
        val HomeAddress: String? = null,
        val ISHr: Int? = 0,
        val IsExecutive: Int? = 0,
        val IsReportingPerson: Int? = 0,
        val LastName: String? = null,
        val Location: String? = null,
        val MaritalID: String? = null,
        val MaritalStatus: String? = null,
        val MiddleName: String? = null,
        val MobileNo: String? = null,
        val MobileNumber: String? = null,
        val MotherDOB: String? = null,
        val MotherName: String? = null,
        val NickName: String? = null,
        val OfficeAddress: String? = null,
        val OfficeConNumber: String? = null,
        val OfficePincode: String? = null,
        val Officialemail: String? = null,
        val Out: Int? = 0,
        val PersonalEmail: String? = null,
        val ProfilePicture: String? = null,
        val ReportingPerson: String? = null,
        val SpouseDOB: String? = null,
        val SpouseName: String? = null,
        val Sublocation: String? = null,
        val UserID: Int? = null,
        val ShowLimitDetails: Boolean? = false,
        val joiningDate: String? = null

//        val ShowLimitDetails: Boolean? = false,








//        val EmployeeFullName: String? = null,
//        val ProfilePicture: String? = null,
//        val Officialemail: String? = null,
//        val joiningDate: String? = null,
//        val FirstName: String? = null,
//        val MiddleName: String? = null,
//        val LastName: String? = null,
//        val NickName: String? = null,
//        val DateOfBirth: String? = null,
//        val EmployeeCode: String? = null,
//        val Designation: String? = null,
//        val ReportingPerson: String? = null,
//        val Department: String? = null,
//        val BranchName: String? = null,
//        val OfficeAddress: String? = null,
//        val OfficePincode: String? = null,
//        val OfficeConNumber: String? = null,
//        val Gender: String? = null,
//        val Genderid: String? = null,
//        val MaritalStatus: String? = null,
//        val MaritalID: String? = null,
//        val HomeAddress: String? = null,
//        val UserID: Int? = null,
//        val MobileNumber: String? = null,
//        val ISHr: Int? = null,
//        val IsReportingPerson: Int? = 0,
//        val IsExecutive: Int? = 0,
//        val Location: String? = null,
//        val Sublocation: String? = null
){
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}