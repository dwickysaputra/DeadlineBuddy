package com.polibatam.synchedule.ui.schedule.detail

import androidx.lifecycle.*
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.data.entity.WeekDayEntity
import com.polibatam.synchedule.data.repository.ScheduleRepository
import com.polibatam.synchedule.data.repository.WeekDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleDetailViewModel @Inject constructor(private val weekDayRepository: WeekDayRepository, private val scheduleRepository: ScheduleRepository) : ViewModel() {

    var startTime: String = "00:00"
    var endTime : String = "00:00"
    var remind : Boolean = true
    var scheduleId = MutableLiveData<Long>()

    var selectedSubjectIndex : Int = 0
    var subjectsData : MutableList<String> = mutableListOf<String>()
    var subjectFullData : MutableList<SubjectEntity> = mutableListOf<SubjectEntity>()

    fun getWeekDays() : LiveData<List<WeekDayEntity>> = weekDayRepository.getWeekdays().asLiveData()
    fun insertWeekDays(weekDays: List<WeekDayEntity>) = viewModelScope.launch { weekDayRepository.insertWeekdays(weekDays) }

    fun getSubjects() : LiveData<List<SubjectEntity>> = scheduleRepository.getSubjectList().asLiveData()

    fun insertSchedule(schedule: ScheduleEntity) = viewModelScope.launch {
        scheduleId.value = scheduleRepository.insertSchedule(schedule)
    }
    fun updateSchedule(schedule: ScheduleEntity) = viewModelScope.launch {
        scheduleRepository.updateSchedule(schedule)
    }

    fun deleteSchedule(scheduleId: Long) = viewModelScope.launch {
        scheduleRepository.deleteSchedule(scheduleId)
    }

    fun getSchedules() : LiveData<List<ScheduleEntity>> = scheduleRepository.getSchedules().asLiveData()

    fun getScheduleAndSubjectById(scheduleId : Long) : LiveData<ScheduleAndSubject?> = scheduleRepository.getScheduleAndSubjectById(scheduleId).asLiveData()
    fun getSubjectAndSchedules(dayId: Long) : LiveData<List<ScheduleAndSubject>> = scheduleRepository.getSubjectAndSchedules(dayId).asLiveData()


}