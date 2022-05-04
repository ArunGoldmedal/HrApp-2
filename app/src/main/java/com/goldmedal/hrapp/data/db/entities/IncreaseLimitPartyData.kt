package com.goldmedal.hrapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IncreaseLimitPartyData(
        @PrimaryKey(autoGenerate = false)
    val cin: String? = null,
    val displaynm: String? = null
)