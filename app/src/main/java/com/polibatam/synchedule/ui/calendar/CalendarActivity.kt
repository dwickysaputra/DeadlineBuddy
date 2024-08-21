package com.polibatam.synchedule.ui.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.polibatam.synchedule.R
import com.polibatam.synchedule.databinding.ActivityCalendarBinding
import com.polibatam.synchedule.databinding.CalendarDayLayoutBinding
import com.polibatam.synchedule.utils.CalendarUtils
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.utils.ActivityCodes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CalendarActivity"
    }

    private var _binding : ActivityCalendarBinding? = null
    private val binding get() = _binding!!
    val newEventDay : ArrayList<EventDay> = ArrayList()

    private val calendarViewModel by viewModels<CalendarViewModel>()
    @Inject lateinit var calendarActivityRecyclerViewAdapter: CalendarActivityRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCalendarBinding.inflate(layoutInflater)
        initAppBar()
        setContentView(binding.root)
        getDeadlineData()
        setRecyclerView()
        setCalendarView()
        setButtonClickListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.title = getString(R.string.menu_item_calendar)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun getDeadlineData(){
         calendarViewModel.getDeadlines().observe(this, { deadlines ->
             deadlines.forEach { deadline ->
                 val calendar = Calendar.getInstance()
                 calendar.timeInMillis = deadline.deadlineTime
                 newEventDay.add(EventDay(calendar, R.drawable.ic_dot_event))
             }
             binding.calendarView.setEvents(newEventDay)
         })
    }

    private fun setRecyclerView(){
        binding.rvListDeadline.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = calendarActivityRecyclerViewAdapter
            setHasFixedSize(false)
        }
    }

    private fun setCalendarView() {
        val currentDate = Calendar.getInstance()
        getDeadlineByDay(currentDate.timeInMillis)
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener{
            override fun onDayClick(eventDay: EventDay) {
                getDeadlineByDay(eventDay.calendar.timeInMillis)
            }

        })
    }

    private fun getDeadlineByDay(day: Long){
        val date = Calendar.getInstance()
        date.timeZone = TimeZone.getDefault()
        date.timeInMillis = day

        val dayInMonth = if(date.get(Calendar.DAY_OF_MONTH) < 10){ "0${date.get(Calendar.DAY_OF_MONTH)}" } else { "${date.get(Calendar.DAY_OF_MONTH)}" }
        val month = if(date.get(Calendar.MONTH) < 10){ "0${date.get(Calendar.MONTH)}" } else { "${date.get(Calendar.MONTH)}" }
        val year = if(date.get(Calendar.YEAR) < 10){ "0${date.get(Calendar.YEAR)}" } else { "${date.get(Calendar.YEAR)}" }
        val monthAdjusted = if((month.toInt() + 1) < 10) { "0${(month.toInt() + 1)}" } else {"${(month.toInt() + 1)}"}

        calendarViewModel.getDeadlinesByDate("$dayInMonth-$monthAdjusted-$year").observe(this, { deadlines ->
            calendarActivityRecyclerViewAdapter.setDeadlines(deadlines)
            calendarActivityRecyclerViewAdapter.notifyDataSetChanged()
        })
    }

    private fun setButtonClickListener(){
        binding.calendarManageLessonBtn.setOnClickListener {
            intentManageCalendar(ActivityCodes.ACTIVITY_ADD, null, null)
        }
        calendarActivityRecyclerViewAdapter.setOnDeadlineItemClick(object: CalendarActivityRecyclerViewAdapter.OnDeadlineItemClickCallback{
            override fun onDeadlineItemClicked(deadlineEntity: DeadlineEntity) {
                intentManageCalendar(ActivityCodes.ACTIVITY_EDIT, deadlineEntity.deadlineId, deadlineEntity.subjectId)
            }
        })
    }

    private fun intentManageCalendar(type: String, deadlineId: Long?, subjectId: Long?){
        val intent = Intent(this, CalendarManageTodayActivity::class.java)
        intent.putExtra(CalendarManageTodayActivity.TYPE, type)
        intent.putExtra(CalendarManageTodayActivity.EXTRA_DEADLINE_ID, deadlineId)
        intent.putExtra(CalendarManageTodayActivity.EXTRA_SUBJECT_ID, subjectId)
        startActivity(intent)
    }

}
