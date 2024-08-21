package com.polibatam.synchedule.utils

import com.polibatam.synchedule.data.entity.WeekDayEntity

object WeekDayData {
    fun setWeekDayData() : List<WeekDayEntity> {
        return listOf(
            WeekDayEntity(1, "Sunday"),
            WeekDayEntity(2, "Monday"),
            WeekDayEntity(3, "Tuesday"),
            WeekDayEntity(4, "Wednesday"),
            WeekDayEntity(5, "Thursday"),
            WeekDayEntity(6, "Friday"),
            WeekDayEntity(7, "Saturday"),
        )
    }

    fun returnDayName(dayId : Long) : String {
        return when(dayId.toInt()){
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> ""
        }
    }
}