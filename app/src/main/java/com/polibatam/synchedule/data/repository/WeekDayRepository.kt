package com.polibatam.synchedule.data.repository

import com.polibatam.synchedule.data.dao.WeekDayDAO
import com.polibatam.synchedule.data.entity.WeekDayEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeekDayRepository @Inject constructor(private val weekDayDAO: WeekDayDAO) {
    fun getWeekdays() : Flow<List<WeekDayEntity>> = weekDayDAO.getWeekdays()

    suspend fun insertWeekdays(weekDays: List<WeekDayEntity>) = weekDayDAO.insertWeekdays(weekDays)
}