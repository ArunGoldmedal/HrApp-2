package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.goldmedal.hrapp.data.db.entities.CURRENT_USER_ID
import com.goldmedal.hrapp.data.db.entities.User
import com.goldmedal.hrapp.data.db.entities.UserDataUpdate


@Dao
interface UserDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User?) : Long

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
     fun getUser() : LiveData<User>



    @Query("UPDATE user SET ProfilePicture = :profilePic")
    suspend fun updateProfilePic(profilePic : String?)

    @Query("DELETE FROM user")
    suspend fun logoutUser()


    @Update(entity = User::class)
    suspend fun update(user: UserDataUpdate?)
}


