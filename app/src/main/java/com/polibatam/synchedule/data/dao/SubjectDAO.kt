package com.polibatam.synchedule.data.dao

import androidx.room.*
import com.polibatam.synchedule.data.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(vararg subject: SubjectEntity)

    @Query("SELECT * FROM subject")
    fun getSubject() : Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subject WHERE subject_id = :id")
    fun getSpecificSubject(id: Long) : Flow<SubjectEntity>

    @Update
    suspend fun updateSubject(vararg subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(vararg subject: SubjectEntity)
}