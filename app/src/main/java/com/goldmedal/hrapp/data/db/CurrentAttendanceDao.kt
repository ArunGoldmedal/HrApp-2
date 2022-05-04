package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.GetCurrentAttendanceData

@Dao
interface CurrentAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAttendanceData(birthData: List<GetCurrentAttendanceData?>) : List<Long>

    @Query("SELECT * FROM GetCurrentAttendanceData")
    fun getCurrentAttendanceData() : LiveData<List<GetCurrentAttendanceData>>

    @Query("DELETE FROM GetCurrentAttendanceData")
    suspend fun removeCurrentAttendanceData()
}