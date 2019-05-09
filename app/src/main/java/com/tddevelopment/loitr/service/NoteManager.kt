package com.tddevelopment.loitr.service

import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.tddevelopment.loitr.R
import java.util.*

class NoteManager {
    companion object {
        fun notify(title: String, message: String, context: Context, noteManager: NotificationManager) {
            val notification = NotificationCompat.Builder(context, "LOITR_CHANNEL")
                .setAutoCancel(true)
                .setContentTitle(message)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_custom_note_icon)
                .build()
            noteManager.notify(Random(10).nextInt(), notification)
        }
    }
}