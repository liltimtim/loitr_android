package com.tddevelopment.loitr.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class GeofenceRepo(private val context: Context) {
    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}