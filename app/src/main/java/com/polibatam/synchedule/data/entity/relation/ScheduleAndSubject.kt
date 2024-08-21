package com.polibatam.synchedule.data.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.data.entity.SubjectEntity

data class ScheduleAndSubject(
    @Embedded val scheduleEntity: ScheduleEntity,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "subject_id"
    )
    val subjectEntity: SubjectEntity
)
