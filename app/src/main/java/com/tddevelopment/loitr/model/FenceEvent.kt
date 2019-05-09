package com.tddevelopment.loitr.model

import android.arch.persistence.room.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
@Entity
data class FenceEvent (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    var type: EventType,
    var date: Date) {

    enum class EventType {
        ENTERED,
        EXITED,
        INVALID;

        fun rawValue(type: EventType): String {
            return when (type) {
                ENTERED -> "enter"
                EXITED -> "exit"
                else -> "invalid"
            }
        }

        override fun toString(): String {
            return rawValue(this)
        }

        companion object {
            fun fromRawValue(type: String): EventType {
                return when (type) {
                    "enter" -> ENTERED
                    "exit" -> EXITED
                    else -> INVALID
                }
            }
        }
    }
}

