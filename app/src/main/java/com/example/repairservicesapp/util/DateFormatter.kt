package com.example.repairservicesapp.util

import com.google.firebase.Timestamp
import java.time.format.DateTimeFormatter

object DateFormatter {
    fun fromTimeStampToHour(timestamp: Timestamp): String {
        // Format the timestamp to a readable format hh:mm a
        val localDateTime = timestamp.toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return localDateTime.format(formatter)
    }
}