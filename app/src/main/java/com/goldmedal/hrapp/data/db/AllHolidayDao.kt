package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.AllHolidayData

@Dao
interface AllHolidayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHoliday(holidayData: List<AllHolidayData?>)

    @Query("SELECT * FROM AllHolidayData")
    fun getAllHoliday() : LiveData<List<AllHolidayData>>

    @Query("DELETE FROM AllHolidayData")
    suspend fun removeAllHoliday()
}