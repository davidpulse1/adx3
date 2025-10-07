package com.example.adsbubble.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import androidx.core.content.ContextCompat
import com.example.adsbubble.geofence.GeofenceTransitionService

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geofencingEvent.hasError()) return
        val transition = geofencingEvent.geofenceTransition
        val triggeringIds = geofencingEvent.triggeringGeofences?.map { it.requestId } ?: emptyList()
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            for (id in triggeringIds) {
                val i = Intent(ctx, GeofenceTransitionService::class.java).apply {
                    putExtra("storeId", id)
                    putExtra("lat", geofencingEvent.triggeringLocation?.latitude ?: 0.0)
                    putExtra("lon", geofencingEvent.triggeringLocation?.longitude ?: 0.0)
                }
                ContextCompat.startForegroundService(ctx, i)
            }
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // handle exit if needed
        }
    }
}
