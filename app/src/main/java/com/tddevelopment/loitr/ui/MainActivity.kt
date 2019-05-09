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
import android.widget.TextView
import com.tddevelopment.loitr.R
import com.tddevelopment.loitr.model.*
import com.tddevelopment.loitr.service.GeofenceApp
import com.tddevelopment.loitr.utils.difference
import kotlinx.coroutines.*
import java.util.*

class MainActivity : BaseActivity() {

    private lateinit var summaryTextView: TextView

    private var timer: Timer? = null

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

        summaryTextView = findViewById(R.id.today_summary_text_view)

        summaryTextView.text = getString(R.string.today_no_data_summary)

        registerFenceMonitoring()

        setupUI()

        setupTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    override fun onResume() {
        super.onResume()
        setupUI()
        setupTimer()
    }

    private fun setupTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                setupUI()
            }
        }, 0, 1000)
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

    private fun setupUI() {
        GlobalScope.launch(Dispatchers.IO) {
            val start = async { arrival(Date()) }.await()
            val end = async { departure(Date()) }.await()
            val summary = summary(start, end)
            var summaryOrNull:String
            withContext(Dispatchers.Main) {
                if (start == null) {
                    summaryTextView.text = getString(R.string.today_no_data_summary)
                } else {
                    if (end != null) {


                        if (summary != null) {
                            summaryOrNull = "${summary?.first}h ${summary?.second}m ${summary?.third}s"
                        } else {
                            summaryOrNull = getString(R.string.today_no_data_summary)
                        }
                        summaryTextView.text = getString(R.string.arrived_departed_summary, start?.toHourString() ?: "", end?.toHourString() ?: "", summaryOrNull)
                    } else {
                        val summary = summary(start, end)
                        summaryOrNull = "${summary?.first}h ${summary?.second}m ${summary?.third}s"
                        summaryTextView.text = getString(R.string.arrived_no_depart_summary, start?.toHourString() ?: "", summaryOrNull)
                    }
                }
            }
        }
    }

    private suspend fun arrival(date: Date) : Date? {
        val events = LoitrDatabase.getInstance(applicationContext)
            .fenceDao()
            .findBetweenDates(
                FenceEvent.EventType.ENTERED.toString(),
                Date().startOfDay().toTimestamp(),
                Date().endOfDay().toTimestamp()
            )
        return events.firstOrNull()?.date
    }

    private suspend fun departure(date: Date) : Date? {
        val events = LoitrDatabase.getInstance(applicationContext)
            .fenceDao()
            .findBetweenDates(
                FenceEvent.EventType.EXITED.toString(),
                Date().startOfDay().toTimestamp(),
                Date().endOfDay().toTimestamp()
            )
        val arrivalEvents = LoitrDatabase.getInstance(applicationContext)
            .fenceDao()
            .findBetweenDates(
                FenceEvent.EventType.ENTERED.toString(),
                Date().startOfDay().toTimestamp(),
                Date().endOfDay().toTimestamp()
            )
        if(arrivalEvents.size == 1 && events.size == 1) { return events.lastOrNull()?.date }

        if(arrivalEvents.size % 2 != 0) { return null }
        // odd number of departure events so we haven't left for real yet.
        // entrance and exit events must have a 1 to 1 matching pair
        if(events.size % 2 != 0) { return null }

        return events.lastOrNull()?.date
    }

    private fun summary(start:Date?, end: Date?): Triple<Long, Long, Long>? {
        if (start == null) { return null }
        return if (end == null) {
            start.difference(Date())
        } else {
            start.difference(end!!)
        }
    }

}
