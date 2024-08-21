package com.polibatam.synchedule.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "subject_id") val id: Long?,
    @ColumnInfo(name = "subject_name") val subjectName: String,
    @ColumnInfo(name = "subject_lecturer") val subjectLecturer : String,
    @ColumnInfo(name = "subject_foreground_color") val subjectForegroundColor : String,
    @ColumnInfo(name = "subject_background_color") val subjectBackgroundColor : String
)
