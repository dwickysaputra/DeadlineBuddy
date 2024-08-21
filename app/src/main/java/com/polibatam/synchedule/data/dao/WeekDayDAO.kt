package com.polibatam.synchedule.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.polibatam.synchedule.data.entity.WeekDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDayDAO {

    @Insert
    suspend fun insertWeekdays(weekDays: List<WeekDayEntity>)

    @Query("SELECT * FROM weekday")
    fun getWeekdays() : Flow<List<WeekDayEntity>>
}