package com.polibatam.synchedule.di

import com.polibatam.synchedule.ui.calendar.CalendarActivityRecyclerViewAdapter
import com.polibatam.synchedule.ui.deadline.DeadlineActivityRecyclerView
import com.polibatam.synchedule.ui.main.MainActivityRecyclerView
import com.polibatam.synchedule.ui.schedule.detail.ScheduleDayRecycleViewAdapter
import com.polibatam.synchedule.ui.schedule.lesson.ScheduleLessonRecyclerViewAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class MainModule {
    @Provides
    fun scheduleLessonAdapter() : ScheduleLessonRecyclerViewAdapter{
        return ScheduleLessonRecyclerViewAdapter()
    }

    @Provides
    fun provideScheduleDayAdapter() : ScheduleDayRecycleViewAdapter{
        return ScheduleDayRecycleViewAdapter()
    }

    @Provides
    fun provideCalendarAdapter() : CalendarActivityRecyclerViewAdapter{
        return CalendarActivityRecyclerViewAdapter()
    }

    @Provides
    fun provideMainAdapter() : MainActivityRecyclerView{
        return MainActivityRecyclerView()
    }

    @Provides
    fun provideDeadlineAdapter() : DeadlineActivityRecyclerView{
        return DeadlineActivityRecyclerView()
    }
}