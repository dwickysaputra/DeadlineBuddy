package com.polibatam.synchedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "deadline",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeadlineEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "deadline_id") val deadlineId : Long?,
    @ColumnInfo(name = "subject_id") val subjectId : Long,
    @ColumnInfo(name = "deadline_title") val deadlineTitle: String,
    @ColumnInfo(name = "deadline_date") val deadlineDate: String,
    @ColumnInfo(name = "deadline_time") val deadlineTime : Long,
    @ColumnInfo(name = "deadline_reminder") val deadlineReminder : Long,
    @ColumnInfo(name = "deadline_note") val deadlineNote: String,
    @ColumnInfo(name = "deadline_status") val deadlineStatus: Boolean = false
)
