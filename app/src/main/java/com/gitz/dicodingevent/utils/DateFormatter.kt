package com.gitz.dicodingevent.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {

    fun formatDateTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return dateString)
        } catch (e: Exception) {
            dateString
        }
    }
}