package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.CURRENT_USER_ID
import com.goldmedal.hrapp.data.db.entities.HolidayData
import com.goldmedal.hrapp.data.db.entities.MyTeamData
import com.goldmedal.hrapp.data.db.entities.User

@Dao
interface MyTeamDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: List<MyTeamData?>)

    @Query("SELECT * FROM MyTeamData")
    fun getMyTeam() : LiveData<List<MyTeamData>>

    @Query("DELETE FROM MyTeamData")
    suspend fun removeTeamData()


}


