package com.tddevelopment.loitr.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.tddevelopment.loitr.R
import com.tddevelopment.loitr.service.GeofenceApp

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val noteManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "NoteChannel"
            val descriptionText = "Notifications for Loitr"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("LOITR_CHANNEL", name, importance).apply { description = descriptionText }
            noteManager.createNotificationChannel(channel)
        }
        registerFenceMonitoring()
    }

    private fun registerFenceMonitoring() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // register geofencing areas

            (application as GeofenceApp).geoClient.removeGeofences(listOf("Work"))
                .addOnSuccessListener {
                    (application as GeofenceApp).registerFences()
                }
                .addOnFailureListener {
                    Log.e(javaClass.simpleName, "Cannot remove fences ${it.localizedMessage}")
                    (application as GeofenceApp).registerFences()
                }

        } else {
            // request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 999)
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            999 -> {
                registerFenceMonitoring()
            }
        }
    }

}
