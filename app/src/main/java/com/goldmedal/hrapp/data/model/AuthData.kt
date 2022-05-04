//package com.goldmedal.hrapp.data.db.entities
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//const val CURRENT_USER_ID = 0
//
//@Entity
//data class User(
//    var id: Int? = null,
//    var name: String? = null,
//    var email: String? = null,
//    var password: String? = null,
//    var email_verified_at: String? = null,
//    var created_at: String? = null,
//    var updated_at: String? = null
//){
//    @PrimaryKey(autoGenerate = false)
//    var uid: Int = CURRENT_USER_ID
//}

package com.goldmedal.hrapp.data.model


import com.google.gson.annotations.SerializedName

data class AuthData(
        @SerializedName("email")
        val email: String?,
        @SerializedName("mobile")
        val mobile: String?
)
