package com.example.adsbubble.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ads")
data class Ad(
    @PrimaryKey val token: String,
    val storeId: String,
    val storeName: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val videoUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val bookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
