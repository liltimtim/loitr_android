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
import com.tddevelopment.loitr.model.FenceDao
import com.tddevelopment.loitr.model.FenceEvent
import com.tddevelopment.loitr.model.LoitrDatabase
import java.util.*


class GeoFenceIntentService : JobIntentService() {

    private lateinit var noteServiceManager: NotificationManager

    companion object {
        fun enqueueWork(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                enqueueWork(context, GeoFenceIntentService::class.java, 9999, intent)
            }
        }
    }

    override fun onHandleWork(p0: Intent) {
        noteServiceManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
                    NoteManager.notify("", "Entered Geofence ${it.requestId}", this, noteServiceManager)
                    LoitrDatabase.getInstance(this).fenceDao().create(FenceEvent(null, FenceEvent.EventType.ENTERED, Date()))
                }

            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                NoteManager.notify("", "Exited Geofence", this, noteServiceManager)
                Log.i(javaClass.simpleName, "User exited geofenced area")
                val fences = geoFenceEvent.triggeringGeofences
                fences.forEach {
                    NoteManager.notify("", "Exiting ID ${it.requestId}", this, noteServiceManager)
                    LoitrDatabase.getInstance(this).fenceDao().create(FenceEvent(null, FenceEvent.EventType.EXITED, Date()))
                }
            }
        }
    }

}