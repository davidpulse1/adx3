package com.example.adsbubble.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AdDao {
    @Query("SELECT * FROM ads ORDER BY timestamp DESC")
    fun getAllAds(): LiveData<List<Ad>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ads: List<Ad>)

    @Update
    suspend fun updateAd(ad: Ad)

    @Query("DELETE FROM ads WHERE token = :token")
    suspend fun deleteByToken(token: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStoreState(state: StoreState)

    @Query("SELECT * FROM store_state WHERE storeId = :storeId")
    suspend fun getStoreState(storeId: String): StoreState?
}
