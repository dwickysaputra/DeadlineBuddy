package com.polibatam.synchedule.ui.deadline

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.relation.DeadlineAndSubject
import com.polibatam.synchedule.data.repository.DeadlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeadlineViewModel @Inject constructor(val deadlineRepository: DeadlineRepository) : ViewModel() {
    var selectedReminderIndex = 0

    fun getCompletedDeadline() : LiveData<List<DeadlineAndSubject>> = deadlineRepository.getCompletedDeadline().asLiveData()

    fun getAllLatestDeadline() : LiveData<List<DeadlineAndSubject>> = deadlineRepository.getAllLatestDeadline().asLiveData()

    fun getInCompleteDeadline() : LiveData<List<DeadlineAndSubject>> = deadlineRepository.getIncompleteDeadline().asLiveData()

    fun setDeadlineComplete(deadlineEntity: DeadlineEntity) = viewModelScope.launch {
        deadlineRepository.updateDeadline(deadlineEntity)
    }

    fun deleteDeadline(deadlineId: Long) = viewModelScope.launch { deadlineRepository.deleteDeadlineById(deadlineId) }
}