package com.polibatam.synchedule.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.polibatam.synchedule.data.dao.SubjectDAO
import com.polibatam.synchedule.data.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepository @Inject constructor(private val subjectDAO: SubjectDAO) {

    fun getSubjects() : Flow<List<SubjectEntity>> = subjectDAO.getSubject()

    fun getSpecificSubject(id: Long) : Flow<SubjectEntity> = subjectDAO.getSpecificSubject(id)

    suspend fun insertNewSubject(subjectEntity: SubjectEntity) = subjectDAO.insertSubject(subjectEntity)

    suspend fun updateSubject(subjectEntity: SubjectEntity) = subjectDAO.updateSubject(subjectEntity)

    suspend fun deleteSubject(subjectEntity: SubjectEntity) = subjectDAO.deleteSubject(subjectEntity)
}