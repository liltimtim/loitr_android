package com.tddevelopment.loitr.model

import android.arch.persistence.room.*
import android.icu.util.LocaleData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Dao
interface FenceDao {
    @Query("SELECT * FROM FenceEvent")
    fun getAll(): List<FenceEvent>

    @Query("SELECT * FROM FenceEvent WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun findBetweenDates(start: String, end: String): List<FenceEvent>

    @Query("SELECT * FROM FenceEvent WHERE date BETWEEN :start AND :end AND type=:eventType ORDER BY date ASC")
    fun findBetweenDates(eventType: String, start: String, end: String): List<FenceEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(event: FenceEvent)

    @Delete
    fun delete(event: FenceEvent)
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.toTimestamp()
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.toTimestamp()
    }

    @TypeConverter
    fun typeToString(eventType: FenceEvent.EventType): String {
        when (eventType) {
            FenceEvent.EventType.ENTERED -> return "enter"
            FenceEvent.EventType.EXITED -> return "exit"
            FenceEvent.EventType.INVALID -> return "invalid"
        }
    }

    @TypeConverter
    fun stringTypeToType(value: String): FenceEvent.EventType {
        when (value) {
            "enter" -> return FenceEvent.EventType.ENTERED
            "exit" -> return FenceEvent.EventType.EXITED
            "invalid" -> return FenceEvent.EventType.INVALID

        }
        return FenceEvent.EventType.INVALID
    }
}

fun String.toTimestamp(): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
    formatter.timeZone = TimeZone.getDefault()
    return formatter.parse(this)
}

fun Date.toTimestamp(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(this)
}

fun Date.toHourString(): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(this)
}

fun Date.startOfDay(): Date {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return cal.time
}

fun Date.endOfDay(): Date {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    return cal.time
}