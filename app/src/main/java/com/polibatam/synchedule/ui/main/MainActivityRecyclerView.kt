package com.polibatam.synchedule.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.databinding.ItemTodayListBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivityRecyclerView : RecyclerView.Adapter<MainActivityRecyclerView.MainActivityRecyclerViewViewHolder>() {

    private val _schedulesAndSubject = ArrayList<ScheduleAndSubject>()

    fun setScheduleData(scheduleAndSubject: List<ScheduleAndSubject>){
        _schedulesAndSubject.clear()
        _schedulesAndSubject.addAll(scheduleAndSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityRecyclerView.MainActivityRecyclerViewViewHolder {
        val binding = ItemTodayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainActivityRecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainActivityRecyclerView.MainActivityRecyclerViewViewHolder, position: Int) {
        holder.setData(_schedulesAndSubject[position])
    }

    override fun getItemCount(): Int = _schedulesAndSubject.size

    inner class MainActivityRecyclerViewViewHolder(val binding: ItemTodayListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(scheduleAndSubject: ScheduleAndSubject){
            with(binding){
                scheduleStartTime.text = setCleanTime(scheduleAndSubject.scheduleEntity.startTime)
                scheduleEndTime.text = setCleanTime(scheduleAndSubject.scheduleEntity.endTime)
                scheduleName.text = scheduleAndSubject.subjectEntity.subjectName
                scheduleLecturer.text = scheduleAndSubject.subjectEntity.subjectLecturer
                scheduleView.setBackgroundColor(Color.parseColor(setColorIndicator(scheduleAndSubject.scheduleEntity.startTime, scheduleAndSubject.scheduleEntity.endTime)))
            }
        }

        private fun setCleanTime(time: Long) : String{
            val startTimeHour = time / 3600
            val startTimeMinute = (time % 3600) / 60
            val cleanStartTimeHour = if(startTimeHour < 10) { "0${startTimeHour}" } else {"$startTimeHour"}
            val cleanStartTimeMinute = if(startTimeMinute < 10) { "0${startTimeMinute}" } else {"$startTimeMinute"}
            return "$cleanStartTimeHour:$cleanStartTimeMinute"
        }

        private fun setColorIndicator(startTime:Long, endTime: Long) : String{
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault()
            val currentTime = (calendar.get(Calendar.HOUR_OF_DAY) * 3600) + (calendar.get(Calendar.MINUTE) * 60) + calendar.get(Calendar.SECOND)
            if(startTime >= currentTime && endTime >= currentTime){
                return "#9E5B5B"
            }

            if(currentTime in endTime..startTime){
                return "#43B640"
            }

            if(startTime <= currentTime && endTime <= currentTime){
                return "#F37121"
            }

            return ""
        }
    }

}