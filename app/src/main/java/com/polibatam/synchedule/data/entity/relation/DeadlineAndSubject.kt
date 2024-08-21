package com.polibatam.synchedule.data.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.SubjectEntity

data class DeadlineAndSubject(
    @Embedded val deadlineEntity: DeadlineEntity,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "subject_id"
    )
    val subjectEntity: SubjectEntity
)
