package com.polibatam.synchedule.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.polibatam.synchedule.data.dao.ScheduleDAO
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    fun getScheduleSubject(dayId: Long) : LiveData<List<ScheduleAndSubject>> = scheduleRepository.getSubjectAndSchedules(dayId).asLiveData()

}