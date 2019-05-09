package com.tddevelopment.loitr.utils

import java.util.*

fun Date.difference(end: Date): Triple<Long, Long, Long> {
    val diff = end.time - this.time
    val seconds = (diff / 1000) % 60
    val minutes = (diff / 1000 / 60) % 60
    val hours = (diff / 1000 / 60 / 60) % 24
    return Triple(hours, minutes, seconds)
}