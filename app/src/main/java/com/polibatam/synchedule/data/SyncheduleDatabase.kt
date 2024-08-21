package com.polibatam.synchedule.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.polibatam.synchedule.data.dao.DeadlineDAO
import com.polibatam.synchedule.data.dao.ScheduleDAO
import com.polibatam.synchedule.data.dao.SubjectDAO
import com.polibatam.synchedule.data.dao.WeekDayDAO
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.data.entity.WeekDayEntity

@Database(entities = [SubjectEntity::class, WeekDayEntity::class, ScheduleEntity::class, DeadlineEntity::class], version = 6)
abstract class SyncheduleDatabase : RoomDatabase() {
    abstract fun subjectDao() : SubjectDAO
    abstract fun weekDayDao() : WeekDayDAO
    abstract fun scheduleDao() : ScheduleDAO
    abstract fun deadlineDao() : DeadlineDAO
}