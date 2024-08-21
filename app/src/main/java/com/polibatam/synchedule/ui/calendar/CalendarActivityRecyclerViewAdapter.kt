package com.polibatam.synchedule.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.databinding.ItemCalendarListBinding
import java.util.*
import kotlin.collections.ArrayList

class CalendarActivityRecyclerViewAdapter : RecyclerView.Adapter<CalendarActivityRecyclerViewAdapter.CalendarActivityViewHolder>() {

    private val _deadlines = ArrayList<DeadlineEntity>()
    private var onDeadlineItemClickCallback : OnDeadlineItemClickCallback? = null

    fun setDeadlines(deadlines: List<DeadlineEntity>){
        _deadlines.clear()
        _deadlines.addAll(deadlines)
    }

    fun setOnDeadlineItemClick(onDeadlineItemClickCallback: OnDeadlineItemClickCallback){
        this.onDeadlineItemClickCallback = onDeadlineItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarActivityViewHolder {
        val binding = ItemCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarActivityViewHolder, position: Int) {
        holder.setData(_deadlines[position])
    }

    override fun getItemCount(): Int = _deadlines.size

    inner class CalendarActivityViewHolder(val binding: ItemCalendarListBinding) : RecyclerView.ViewHolder(binding.root){
        fun setData(deadlineEntity: DeadlineEntity){
            with(binding){
                val date = deadlineEntity.deadlineDate.split("-")
                val timeCalendar = Calendar.getInstance()
                timeCalendar.timeInMillis = deadlineEntity.deadlineTime

                val timeHour = if(timeCalendar.get(Calendar.HOUR_OF_DAY) < 10) { "0${timeCalendar.get(Calendar.HOUR_OF_DAY)}" } else {"${timeCalendar.get(Calendar.HOUR_OF_DAY)}"}
                val timeMinute = if(timeCalendar.get(Calendar.MINUTE) < 10) { "0${timeCalendar.get(Calendar.MINUTE)}" } else {"${timeCalendar.get(Calendar.MINUTE)}"}

                itemCalendarDate.text = date[0]
                itemCalendarTitle.text = deadlineEntity.deadlineTitle
                itemCalendarTime.text = "$timeHour:$timeMinute"

                itemView.setOnClickListener { onDeadlineItemClickCallback?.onDeadlineItemClicked(deadlineEntity) }
            }
        }
    }

    interface OnDeadlineItemClickCallback {
        fun onDeadlineItemClicked(deadlineEntity: DeadlineEntity)
    }
}