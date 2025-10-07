package com.example.adsbubble.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_state")
data class StoreState(
    @PrimaryKey val storeId: String,
    val isInside: Boolean,
    val lastEnterTs: Long? = null
)
