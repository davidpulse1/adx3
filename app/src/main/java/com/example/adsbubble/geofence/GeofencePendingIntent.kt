package com.example.adsbubble.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object GeofencePendingIntent {
    private var intent: PendingIntent? = null
    fun getPendingIntent(context: Context, storeId: String): PendingIntent {
        if (intent != null) return intent!!
        val i = Intent(context, GeofenceBroadcastReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        intent = PendingIntent.getBroadcast(context, storeId.hashCode(), i, flags)
        return intent!!
    }
}
