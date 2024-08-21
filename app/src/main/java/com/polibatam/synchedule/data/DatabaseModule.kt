package com.polibatam.synchedule.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideSyncheduleDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, SyncheduleDatabase::class.java, "SyncheduleDB").build()

    @Singleton
    @Provides
    fun provideSubjectDao(db: SyncheduleDatabase) = db.subjectDao()

    @Singleton
    @Provides
    fun provideWeekDayDAO(db: SyncheduleDatabase) = db.weekDayDao()

    @Singleton
    @Provides
    fun provideScheduleDAO(db: SyncheduleDatabase) = db.scheduleDao()

    @Singleton
    @Provides
    fun provideDeadlineDAO(db: SyncheduleDatabase) = db.deadlineDao()
}