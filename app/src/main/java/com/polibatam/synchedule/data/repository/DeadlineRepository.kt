package com.polibatam.synchedule.data.repository

import com.polibatam.synchedule.data.dao.DeadlineDAO
import com.polibatam.synchedule.data.dao.SubjectDAO
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.data.entity.relation.DeadlineAndSubject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeadlineRepository @Inject constructor(private val deadlineDAO: DeadlineDAO, private val subjectDAO: SubjectDAO) {

    suspend fun insertDeadline(deadlineEntity: DeadlineEntity) : Long = deadlineDAO.insertDeadline(deadlineEntity)
    fun getDeadlines() : Flow<List<DeadlineEntity>> = deadlineDAO.getDeadlines()
    fun getDeadlinesByDate(date: String) : Flow<List<DeadlineEntity>> = deadlineDAO.getDeadlinesByDate(date)
    fun getDeadlineById(id: Long) : Flow<DeadlineEntity> = deadlineDAO.getDeadlineById(id)
    suspend fun updateDeadline(deadlineEntity: DeadlineEntity) = deadlineDAO.updateDeadline(deadlineEntity)
    suspend fun deleteDeadlineById(deadlineId: Long) = deadlineDAO.deleteDeadlineById(deadlineId)

    fun getAllLatestDeadline() : Flow<List<DeadlineAndSubject>> = deadlineDAO.getAllLatestDeadline()
    fun getCompletedDeadline() : Flow<List<DeadlineAndSubject>> = deadlineDAO.getCompletedDeadline()
    fun getIncompleteDeadline() : Flow<List<DeadlineAndSubject>> = deadlineDAO.getIncompleteDeadline()

    fun getSubjects() : Flow<List<SubjectEntity>> = subjectDAO.getSubject()

}