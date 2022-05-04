package com.goldmedal.hrapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.hrapp.data.db.entities.AnniversaryData


@Dao
interface AnniversaryDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnniversaryDate(birthData: List<AnniversaryData?>) : List<Long>

    @Query("SELECT * FROM AnniversaryData")
    fun getAnniversaryDate() : LiveData<List<AnniversaryData>>

    @Query("DELETE FROM AnniversaryData")
    suspend fun removeAnniversaryData()

}


