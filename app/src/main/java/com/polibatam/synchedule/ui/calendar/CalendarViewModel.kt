package com.polibatam.synchedule.ui.calendar

import android.util.Log
import androidx.lifecycle.*
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.data.repository.DeadlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val deadlineRepository: DeadlineRepository) : ViewModel() {
    var date: Long = Calendar.getInstance(TimeZone.getDefault()).timeInMillis
    var dateString : String = ""
    var time: Long = Calendar.getInstance(TimeZone.getDefault()).timeInMillis
    var title : String = ""
    var note : String = ""
    var selectedReminder : Int = 0
    var selectedSubjectIndex : Int = 0
    var subjectsData : MutableList<String> = mutableListOf<String>()
    var subjectFullData : MutableList<SubjectEntity> = mutableListOf<SubjectEntity>()

    val deadlineId : MutableLiveData<Long> = MutableLiveData()

    fun getSubjects() : LiveData<List<SubjectEntity>> = deadlineRepository.getSubjects().asLiveData()

    fun addDeadline(deadlineEntity: DeadlineEntity) = viewModelScope.launch {
        deadlineId.value = deadlineRepository.insertDeadline(deadlineEntity)
    }

    fun updateDeadline(deadlineEntity: DeadlineEntity) = viewModelScope.launch {
        deadlineRepository.updateDeadline(deadlineEntity)
    }

    fun deleteDeadlineById(deadlineId : Long) = viewModelScope.launch {
        deadlineRepository.deleteDeadlineById(deadlineId)
    }

    fun getDeadlines() : LiveData<List<DeadlineEntity>> = deadlineRepository.getDeadlines().asLiveData()

    fun getDeadlinesByDate(date: String) : LiveData<List<DeadlineEntity>> {
        return deadlineRepository.getDeadlinesByDate(date).asLiveData()
    }

    fun getDeadlineById(id: Long) : LiveData<DeadlineEntity> = deadlineRepository.getDeadlineById(id).asLiveData()
}