package com.example.adsbubble.geofence

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceHelper(private val context: Context) {
    private val geofencingClient by lazy { LocationServices.getGeofencingClient(context) }

    fun registerGeofence(storeId: String, lat: Double, lon: Double, radiusMeters: Float = 1609f) {
        val geofence = Geofence.Builder()
            .setRequestId(storeId)
            .setCircularRegion(lat, lon, radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

        val pi = GeofencePendingIntent.getPendingIntent(context, storeId)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        geofencingClient.addGeofences(request, pi)
    }

    fun removeGeofence(storeId: String) {
        geofencingClient.removeGeofences(listOf(storeId))
    }
}
