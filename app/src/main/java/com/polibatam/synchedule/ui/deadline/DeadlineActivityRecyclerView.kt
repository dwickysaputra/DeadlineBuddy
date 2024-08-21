package com.polibatam.synchedule.ui.deadline

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.relation.DeadlineAndSubject
import com.polibatam.synchedule.databinding.ItemDeadlineListBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DeadlineActivityRecyclerView : RecyclerView.Adapter<DeadlineActivityRecyclerView.DeadlineViewHolder>() {

    private val _deadlineAndSubject = ArrayList<DeadlineAndSubject>()
    private var onDeadlineMenuClickCallback : OnDeadlineMenuClickCallback? = null

    fun setDeadlineData(deadlineAndSubject: List<DeadlineAndSubject>){
        _deadlineAndSubject.clear()
        _deadlineAndSubject.addAll(deadlineAndSubject)
    }

    fun setOnDeadlineMenuClickCallback(onDeadlineMenuClickCallback: OnDeadlineMenuClickCallback){
        this.onDeadlineMenuClickCallback = onDeadlineMenuClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeadlineActivityRecyclerView.DeadlineViewHolder {
        val binding = ItemDeadlineListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeadlineActivityRecyclerView.DeadlineViewHolder, position: Int) {
        holder.bindData(_deadlineAndSubject[position])
    }

    override fun getItemCount(): Int = _deadlineAndSubject.size

    inner class DeadlineViewHolder(val binding: ItemDeadlineListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(deadlineAndSubject: DeadlineAndSubject){
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            resetBinding()
            // completed
            if(deadlineAndSubject.deadlineEntity.deadlineStatus){
                deadlineFinish(deadlineAndSubject)
            }

            // expired
            if(!deadlineAndSubject.deadlineEntity.deadlineStatus && calendar.timeInMillis >= deadlineAndSubject.deadlineEntity.deadlineTime){
                deadlineExpired(deadlineAndSubject)
            }

            // incomplete
            if(!deadlineAndSubject.deadlineEntity.deadlineStatus && calendar.timeInMillis <= deadlineAndSubject.deadlineEntity.deadlineTime){
                deadlineIncomplete(deadlineAndSubject, calendar)
            }

            binding.deadlineMenu.setOnClickListener { onDeadlineMenuClickCallback?.setOnDeadlineMenuItemClicked(deadlineAndSubject, binding.deadlineDropdownView) }
        }

        private fun resetBinding(){
            binding.deadlineTitle.paintFlags = 0
            binding.deadlineFinished.visibility = View.VISIBLE
            binding.deadlineExpired.visibility = View.VISIBLE
            binding.deadlineDate.visibility = View.VISIBLE
            binding.deadlineTime.visibility = View.VISIBLE
            binding.deadlineRemainTime.visibility = View.VISIBLE
        }

        private fun deadlineFinish(deadlineAndSubject: DeadlineAndSubject){
            binding.deadlineTitle.text = deadlineAndSubject.deadlineEntity.deadlineTitle
            binding.deadlineTitle.paintFlags = binding.deadlineTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.deadlineFinished.text = "Finished - ${returnDeadlineDate(deadlineAndSubject.deadlineEntity.deadlineTime)}"
            binding.deadlineSubject.text = deadlineAndSubject.subjectEntity.subjectName
            binding.deadlineExpired.visibility = View.GONE
            binding.deadlineDate.visibility = View.GONE
            binding.deadlineTime.visibility = View.GONE
            binding.deadlineRemainTime.visibility = View.INVISIBLE
        }

        private fun deadlineExpired(deadlineAndSubject: DeadlineAndSubject){
            binding.deadlineTitle.text = deadlineAndSubject.deadlineEntity.deadlineTitle
            binding.deadlineExpired.text = "Expired - ${returnDeadlineDate(deadlineAndSubject.deadlineEntity.deadlineTime)}"
            binding.deadlineSubject.text = deadlineAndSubject.subjectEntity.subjectName
            binding.deadlineFinished.visibility = View.GONE
            binding.deadlineDate.visibility = View.GONE
            binding.deadlineTime.visibility = View.GONE
            binding.deadlineRemainTime.visibility = View.INVISIBLE
        }

        private fun deadlineIncomplete(deadlineAndSubject: DeadlineAndSubject, currentTime : Calendar){
            val deadlineCalendar = Calendar.getInstance(TimeZone.getDefault())
            deadlineCalendar.timeInMillis = deadlineAndSubject.deadlineEntity.deadlineTime

            val diffInMils = (deadlineCalendar.timeInMillis - currentTime.timeInMillis)
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMils)
            val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMils)

            binding.deadlineTitle.text = deadlineAndSubject.deadlineEntity.deadlineTitle
            binding.deadlineRemainTime.text = returnDeadlineRemainTime(diffInDays, diffInHours)
            binding.deadlineDate.text = returnDeadlineDate(deadlineAndSubject.deadlineEntity.deadlineTime)
            binding.deadlineTime.text = returnDeadlineTime(deadlineCalendar)
            binding.deadlineSubject.text = deadlineAndSubject.subjectEntity.subjectName

            binding.deadlineFinished.visibility = View.GONE
            binding.deadlineExpired.visibility = View.GONE
        }

        private fun returnDeadlineDate(date: Long) : String{
            val currentTime = Calendar.getInstance(TimeZone.getDefault())
            currentTime.timeInMillis = date

            return SimpleDateFormat("dd LLLL yyyy ", Locale.getDefault()).format(currentTime.time)
        }

        private fun returnDeadlineTime(deadline: Calendar) : String{
            val hour = deadline.get(Calendar.HOUR_OF_DAY)
            val minute = deadline.get(Calendar.MINUTE)

            val formattedHour = if(hour < 10){ "0$hour" } else { "$hour" }
            val formattedMinute = if(minute < 10) { "0$minute" } else { "$minute" }

            return "$formattedHour:$formattedMinute"
        }

        private fun returnDeadlineRemainTime(days: Long, hours: Long) : String{
            return when{
                days > 1 -> {
                    "$days Days Left"
                }
                days.toInt() == 1 -> {
                    "$days Day Left"
                }
                hours.toInt() > 0 -> {
                    "$hours Hours Left"
                }
                else -> "$hours Hour Left"
            }
        }
    }

    interface OnDeadlineMenuClickCallback{
        fun setOnDeadlineMenuItemClicked(deadlineAndSubject: DeadlineAndSubject, view: View)
    }

}