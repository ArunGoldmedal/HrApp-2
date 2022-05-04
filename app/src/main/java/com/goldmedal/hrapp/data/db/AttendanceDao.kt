package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.GetAllAttendanceData

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAttendanceData(birthData: List<GetAllAttendanceData?>) : List<Long>

    @Query("SELECT * FROM GetAllAttendanceData")
    fun getAttendanceData() : LiveData<List<GetAllAttendanceData>>

    @Query("DELETE FROM GetAllAttendanceData")
    suspend fun removeAttendanceData()
}