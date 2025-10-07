package com.example.adsbubble.geofence

import android.app.IntentService
import android.content.Intent
import com.example.adsbubble.data.api.ApiClient
import com.example.adsbubble.data.db.AppDatabase
import com.example.adsbubble.data.repo.AdRepository
import kotlinx.coroutines.runBlocking

class GeofenceTransitionService : IntentService("GeofenceTransitionService") {
    override fun onHandleIntent(intent: Intent?) {
        val storeId = intent?.getStringExtra("storeId") ?: return
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val db = AppDatabase.getInstance(applicationContext)
        val repo = AdRepository(ApiClient.retrofit, db.adDao(), applicationContext.getSharedPreferences("prefs", 0))
        runBlocking {
            try {
                repo.refreshAds(lat, lon)
                repo.markEntered(storeId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
