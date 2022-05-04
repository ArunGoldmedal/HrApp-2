package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.BirthdayData


@Dao
interface BirthdayDao{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirthDate(birthData: List<BirthdayData?>)

    @Query("SELECT * FROM BirthdayData")
    fun getBirthDate() : LiveData<List<BirthdayData>>

    @Query("DELETE FROM BirthdayData")
    suspend fun removeBirthDate()
}


