package com.tddevelopment.loitr.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.tddevelopment.loitr.R
import java.util.*


class GeoFenceIntentService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                enqueueWork(context, GeoFenceIntentService::class.java, 9999, intent)
            }
        }
    }

    override fun onHandleWork(p0: Intent) {
        val geoFenceEvent = GeofencingEvent.fromIntent(p0)
        if(geoFenceEvent.hasError()) {
            val message = GeofenceStatusCodes.getStatusCodeString(geoFenceEvent.errorCode)
            Log.e(javaClass.simpleName, message)
            return
        }

        val transition = geoFenceEvent.geofenceTransition

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.i(javaClass.simpleName, "User entered geofenced area")
                val fences = geoFenceEvent.triggeringGeofences
                fences.forEach {
                    Log.i(javaClass.simpleName, "Entered ID ${it.requestId}")
                    notificationFire("Entered Geofence ${it.requestId}")
                }

            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationFire("Exited Geofence")
                Log.i(javaClass.simpleName, "User exited geofenced area")
                val fences = geoFenceEvent.triggeringGeofences
                fences.forEach {
                    Log.i(javaClass.simpleName, "Exiting ID ${it.requestId}")
                    notificationFire("Exited ID ${it.requestId}")
                }
            }
        }
    }

    private fun notificationFire(message: String) {
        val notification = NotificationCompat.Builder(this, "LOITR_CHANNEL")
            .setAutoCancel(true)
            .setContentTitle(message)
            .setContentText(message)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_custom_note_icon)
            .build()
        val noteManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noteManager.notify(Random(10).nextInt(), notification)
    }

}