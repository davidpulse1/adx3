package com.example.adsbubble.data.api

import com.example.adsbubble.data.model.AdDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

data class AdsResponse(val ads: List<AdDto>, val hash: String? = null)

interface ApiService {
    @GET("ads/nearby")
    suspend fun getNearbyAds(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("radius") radiusMiles: Double = 1.0): AdsResponse

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Map<String, String>

    @Multipart
    @POST("ads/upload")
    suspend fun uploadAd(
        @Header("Authorization") bearer: String,
        @Part("storeId") storeId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("templateId") templateId: RequestBody,
        @Part media: MultipartBody.Part?
    ): Map<String, Any>
}
