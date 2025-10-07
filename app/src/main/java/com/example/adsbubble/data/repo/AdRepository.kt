package com.example.adsbubble.data.repo

import android.content.Context
import android.content.SharedPreferences
import com.example.adsbubble.data.api.ApiClient
import com.example.adsbubble.data.api.ApiService
import com.example.adsbubble.data.db.Ad
import com.example.adsbubble.data.db.Ad as RoomAd
import com.example.adsbubble.data.db.AdDao
import com.example.adsbubble.data.db.StoreState
import com.example.adsbubble.data.model.AdDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdRepository(private val api: ApiService, private val dao: AdDao, private val prefs: SharedPreferences) {

    fun allAds() = dao.getAllAds()

    private fun dtoToRoom(a: AdDto): RoomAd {
        return RoomAd(
            token = a.token,
            storeId = a.storeId,
            storeName = a.storeName,
            title = a.title,
            description = a.description,
            imageUrl = a.imageUrl,
            videoUrl = a.videoUrl,
            latitude = a.latitude,
            longitude = a.longitude
        )
    }

    suspend fun refreshAds(lat: Double, lon: Double) = withContext(Dispatchers.IO) {
        val resp = api.getNearbyAds(lat, lon)
        val newHash = resp.hash ?: resp.ads.hashCode().toString()
        val key = "ads_hash_${lat.toString().take(8)}_${lon.toString().take(8)}"
        val last = prefs.getString(key, null)
        if (last != newHash) {
            val list = resp.ads.map { dtoToRoom(it) }
            dao.insertAll(list)
            prefs.edit().putString(key, newHash).apply()
        }
    }

    suspend fun markEntered(storeId: String) {
        dao.upsertStoreState(StoreState(storeId, true, System.currentTimeMillis()))
    }

    suspend fun markExited(storeId: String) {
        dao.upsertStoreState(StoreState(storeId, false, null))
    }

    suspend fun updateAd(ad: RoomAd) {
        dao.updateAd(ad)
    }

    suspend fun deleteByToken(token: String) = dao.deleteByToken(token)
}
