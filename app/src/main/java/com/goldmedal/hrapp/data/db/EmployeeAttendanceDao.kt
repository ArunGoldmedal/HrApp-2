package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.EmployeeAttendanceData

@Dao
interface EmployeeAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpAttendanceData(empAttData: List<EmployeeAttendanceData?>): List<Long>

    @Query("SELECT * FROM EmployeeAttendanceData")
    fun getEmpAttendanceData(): LiveData<List<EmployeeAttendanceData>>

    @Query("DELETE FROM EmployeeAttendanceData")
    suspend fun removeEmpAttendanceData()

}