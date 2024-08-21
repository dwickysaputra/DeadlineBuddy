package com.polibatam.synchedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekday")
data class WeekDayEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "day_id") val dayId : Long,
    @ColumnInfo(name = "day_name") val dayName: String
)
