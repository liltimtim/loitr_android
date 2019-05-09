package com.tddevelopment.loitr.service

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.tddevelopment.loitr.R
import java.util.*

class GeofenceApp : Application() {

    lateinit var geoClient: GeofencingClient

    private lateinit var geofenceRepo: GeofenceRepo

    override fun onCreate() {
        super.onCreate()
        geoClient = LocationServices.getGeofencingClient(this)
        geofenceRepo = GeofenceRepo(this)
    }

    fun getRepo() = geofenceRepo

    @SuppressLint("MissingPermission")
    fun registerFences() {
        val fenceEnter = Geofence.Builder()
            .setRequestId("Work Enter")
            .setCircularRegion(33.4770, -81.9688, 250f)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setNotificationResponsiveness(5000)
            .build()
        val fenceExit = Geofence.Builder()
            .setRequestId("Work Exit")
            .setCircularRegion(33.4770, -81.9688, 250f)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setNotificationResponsiveness(5000)
            .build()
//        val fenceLoiter = Geofence.Builder()
//            .setRequestId("Work Dwell")
//            .setCircularRegion(33.4770, -81.9688, 250f)
//            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
//            .setExpirationDuration(Geofence.NEVER_EXPIRE)
//            .setLoiteringDelay(5000)
//            .setNotificationResponsiveness(5000)
//            .build()
        val fenceIntent = getRepo().geofencePendingIntent
        geoClient.addGeofences(
            GeofencingRequest.Builder().setInitialTrigger(Geofence.GEOFENCE_TRANSITION_DWELL).addGeofences(
                listOf(fenceEnter, fenceExit)
            ).build(), fenceIntent
        ).run {
            addOnSuccessListener {
                Log.i(javaClass.simpleName, "Successfully registered fence")
                val notification = NotificationCompat.Builder(applicationContext, "LOITR_CHANNEL")
                    .setAutoCancel(true)
                    .setContentTitle("Finished Fence Registration")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_custom_note_icon)
                    .build()
                val noteManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                noteManager.notify(Random(1).nextInt(), notification)

            }
            addOnFailureListener {
                Log.i(javaClass.simpleName, "Failed to add fences with reason ${it.message}")
            }
        }
    }
}