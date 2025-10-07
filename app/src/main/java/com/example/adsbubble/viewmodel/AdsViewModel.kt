package com.example.adsbubble.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.adsbubble.data.api.ApiClient
import com.example.adsbubble.data.db.AppDatabase
import com.example.adsbubble.data.db.Ad
import com.example.adsbubble.data.repo.AdRepository
import kotlinx.coroutines.launch

class AdsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val prefs = application.getSharedPreferences("prefs", 0)
    private val repo = AdRepository(ApiClient.retrofit, db.adDao(), prefs)

    val ads: LiveData<List<Ad>> = repo.allAds()

    fun refresh(lat: Double, lon: Double) {
        viewModelScope.launch {
            repo.refreshAds(lat, lon)
        }
    }

    fun markEntered(storeId: String) {
        viewModelScope.launch { repo.markEntered(storeId) }
    }

    fun markExited(storeId: String) {
        viewModelScope.launch { repo.markExited(storeId) }
    }

    fun deleteToken(token: String) {
        viewModelScope.launch { repo.deleteByToken(token) }
    }
}
