package com.example.adsbubble.data.model

data class AdDto(
    val token: String,
    val storeId: String,
    val storeName: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val videoUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radiusMeters: Int? = 1609
)
