package com.polibatam.synchedule.utils

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

object CalendarUtils {
    fun dateToString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}