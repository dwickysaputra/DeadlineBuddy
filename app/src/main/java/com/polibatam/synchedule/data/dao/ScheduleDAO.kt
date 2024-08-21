package com.polibatam.synchedule.data.dao

import androidx.room.*
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSchedule(schedule: ScheduleEntity) : Long

    @Update
    suspend fun updateSchedule(vararg schedule: ScheduleEntity)

    @Query("DELETE FROM schedule WHERE schedule_id = :scheduleId")
    suspend fun deleteScheduleById(scheduleId: Long)

    @Query("SELECT * FROM schedule")
    fun getSchedules() : Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedule WHERE day_id = :dayId")
    fun getScheduleByDay(dayId : Long) : Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedule WHERE schedule_id = :scheduleId")
    fun getScheduleAndSubjectById(scheduleId : Long) : Flow<ScheduleAndSubject>

    @Transaction
    @Query("SELECT * FROM schedule WHERE day_id = :dayId")
    fun getSubjectAndSchedules(dayId: Long): Flow<List<ScheduleAndSubject>>
}