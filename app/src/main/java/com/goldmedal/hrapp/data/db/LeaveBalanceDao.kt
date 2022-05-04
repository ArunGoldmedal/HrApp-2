package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.LeaveBalanceData

@Dao
interface LeaveBalanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveBalanceData(leaveBalData: List<LeaveBalanceData?>): List<Long>

    @Query("SELECT * FROM LeaveBalanceData")
    fun getLeaveBalanceData(): LiveData<List<LeaveBalanceData>>

    @Query("DELETE FROM LeaveBalanceData")
    suspend fun removeLeaveBalanceData()
}
