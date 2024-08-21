package com.polibatam.synchedule.ui.schedule.lesson

import android.util.Log
import androidx.lifecycle.*
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleLessonViewModel @Inject constructor(private val subjectRepository: SubjectRepository) : ViewModel() {
    var subjectName : String? = ""
    var subjectLecturer : String? = ""
    var subjectBackgroundColor : String? = "#F37121"
    var subjectForegroundColor : String? = "#FFFFFF"

    val lessons : LiveData<List<SubjectEntity>> = subjectRepository.getSubjects().asLiveData()

    fun insertLesson() = viewModelScope.launch {
        subjectRepository.insertNewSubject(
            SubjectEntity(null, subjectName!!, subjectLecturer!!, subjectForegroundColor!!, subjectBackgroundColor!!)
        )
    }

    fun getSpecificLesson(id: Long) : LiveData<SubjectEntity?> = subjectRepository.getSpecificSubject(id).asLiveData()

    fun updateLesson(subjectEntity: SubjectEntity) = viewModelScope.launch {
        subjectRepository.updateSubject(subjectEntity)
    }

    fun deleteLesson(subjectEntity: SubjectEntity) = viewModelScope.launch { subjectRepository.deleteSubject(subjectEntity) }
}