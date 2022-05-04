package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.HolidayData


@Dao
interface HolidayDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holidayData: List<HolidayData?>)

    @Query("SELECT * FROM HolidayData")
    fun getHoliday() : LiveData<List<HolidayData>>

    @Query("DELETE FROM HolidayData")
    suspend fun removeHoliday()
}


