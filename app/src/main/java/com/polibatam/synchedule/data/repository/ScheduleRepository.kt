package com.polibatam.synchedule.data.repository

import com.polibatam.synchedule.data.dao.ScheduleDAO
import com.polibatam.synchedule.data.dao.SubjectDAO
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.data.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepository @Inject constructor(private val scheduleDAO: ScheduleDAO, private val subjectDAO: SubjectDAO) {

    suspend fun insertSchedule(scheduleEntity: ScheduleEntity) : Long = scheduleDAO.insertSchedule(scheduleEntity)
    suspend fun updateSchedule(scheduleEntity: ScheduleEntity) = scheduleDAO.updateSchedule(scheduleEntity)
    suspend fun deleteSchedule(scheduleId: Long) = scheduleDAO.deleteScheduleById(scheduleId)
    fun getSchedules() : Flow<List<ScheduleEntity>> = scheduleDAO.getSchedules()

    fun getScheduleAndSubjectById(scheduleId: Long) : Flow<ScheduleAndSubject> = scheduleDAO.getScheduleAndSubjectById(scheduleId)

    fun getSubjectList() : Flow<List<SubjectEntity>> = subjectDAO.getSubject()
    fun getSubjectAndSchedules(dayId : Long) : Flow<List<ScheduleAndSubject>> = scheduleDAO.getSubjectAndSchedules(dayId)
}