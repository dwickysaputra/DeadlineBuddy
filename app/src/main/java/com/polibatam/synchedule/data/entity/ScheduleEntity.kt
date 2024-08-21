package com.polibatam.synchedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WeekDayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"]
        )
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "schedule_id") val scheduleId : Long?,
    @ColumnInfo(name = "day_id") val dayId : Long,
    @ColumnInfo(name = "subject_id") val subjectId : Long,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long,
    @ColumnInfo(name = "remind") val remind : Boolean = true,
)
