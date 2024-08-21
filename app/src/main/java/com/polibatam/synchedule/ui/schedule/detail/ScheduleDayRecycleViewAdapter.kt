package com.polibatam.synchedule.ui.schedule.detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.databinding.ItemTodayListBinding

class ScheduleDayRecycleViewAdapter : RecyclerView.Adapter<ScheduleDayRecycleViewAdapter.ScheduleDayViewHolder>() {

    private var _scheduleAndSubjects = ArrayList<ScheduleAndSubject>()
    private var onItemScheduleClickCallback : OnItemScheduleClickCallback? = null

    fun setSchedules(scheduleAndSubject: List<ScheduleAndSubject>){
        _scheduleAndSubjects.clear()
        _scheduleAndSubjects.addAll(scheduleAndSubject)
    }

    fun setOnItemClickCallback(onItemScheduleClickCallback: OnItemScheduleClickCallback){
        this.onItemScheduleClickCallback = onItemScheduleClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleDayViewHolder {
        val binding = ItemTodayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleDayViewHolder, position: Int) {
        holder.setData(_scheduleAndSubjects[position])
    }

    override fun getItemCount(): Int = _scheduleAndSubjects.size

    inner class ScheduleDayViewHolder(private val binding: ItemTodayListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(scheduleAndSubject: ScheduleAndSubject){
            with(binding){
                scheduleName.text = scheduleAndSubject.subjectEntity.subjectName
                scheduleLecturer.text = scheduleAndSubject.subjectEntity.subjectLecturer
                scheduleStartTime.text = setCleanTime(scheduleAndSubject.scheduleEntity.startTime)
                scheduleEndTime.text = setCleanTime(scheduleAndSubject.scheduleEntity.endTime)
                scheduleView.setBackgroundColor(Color.parseColor(scheduleAndSubject.subjectEntity.subjectBackgroundColor))
                scheduleStartTime.setTextColor(Color.parseColor(scheduleAndSubject.subjectEntity.subjectForegroundColor))
                scheduleEndTime.setTextColor(Color.parseColor(scheduleAndSubject.subjectEntity.subjectForegroundColor))

                itemView.setOnClickListener { onItemScheduleClickCallback?.onItemScheduleClicked(scheduleAndSubject) }
            }
        }

        private fun setCleanTime(time: Long) : String{
            val startTimeHour = time / 3600
            val startTimeMinute = (time % 3600) / 60
            val cleanStartTimeHour = if(startTimeHour < 10) { "0${startTimeHour}" } else {"$startTimeHour"}
            val cleanStartTimeMinute = if(startTimeMinute < 10) { "0${startTimeMinute}" } else {"$startTimeMinute"}
            return "$cleanStartTimeHour:$cleanStartTimeMinute"
        }
    }

    interface OnItemScheduleClickCallback {
        fun onItemScheduleClicked(scheduleAndSubject: ScheduleAndSubject)
    }
}