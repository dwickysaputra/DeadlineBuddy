package com.polibatam.synchedule.data.dao

import androidx.room.*
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.relation.DeadlineAndSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface DeadlineDAO {

    @Insert
    suspend fun insertDeadline(deadlineEntity: DeadlineEntity) : Long

    @Query("SELECT * FROM deadline")
    fun getDeadlines() : Flow<List<DeadlineEntity>>

    @Query("SELECT * FROM deadline WHERE deadline_date = :date")
    fun getDeadlinesByDate(date: String) : Flow<List<DeadlineEntity>>

    @Query("SELECT * FROM deadline WHERE deadline_id = :id")
    fun getDeadlineById(id: Long) : Flow<DeadlineEntity>

    @Update
    suspend fun updateDeadline(deadlineEntity: DeadlineEntity)

    @Query("DELETE FROM deadline WHERE deadline_id = :deadlineId")
    suspend fun deleteDeadlineById(deadlineId: Long)

    @Transaction
    @Query("SELECT * FROM deadline ORDER BY deadline_date DESC")
    fun getAllLatestDeadline() : Flow<List<DeadlineAndSubject>>

    @Transaction
    @Query("SELECT * FROM deadline WHERE deadline_status = 1 ORDER BY deadline_date DESC")
    fun getCompletedDeadline() : Flow<List<DeadlineAndSubject>>

    @Transaction
    @Query("SELECT * FROM deadline WHERE deadline_status = 0  ORDER BY deadline_date DESC")
    fun getIncompleteDeadline() : Flow<List<DeadlineAndSubject>>
}
