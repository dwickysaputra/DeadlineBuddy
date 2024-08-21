package com.polibatam.synchedule.ui.schedule.detail

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.card.MaterialCardView
import com.polibatam.synchedule.R
import com.polibatam.synchedule.databinding.ActivityScheduleDetailBinding
import com.polibatam.synchedule.utils.WeekDayData
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ScheduleDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScheduleDetailActivity"
    }

    private var _binding : ActivityScheduleDetailBinding? = null
    private val binding get() = _binding!!
    private val scheduleDetailViewModel  by viewModels<ScheduleDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleDetailBinding.inflate(layoutInflater)
        initAppBar()
        setContentView(binding.root)
        checkTableExist()
        checkCurrentDay()
        getDayActivityCount()
        setButtonClickListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.title = "Daily Schedule"
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun checkTableExist(){
        scheduleDetailViewModel.getWeekDays().observe(this, { weekDays ->
            if(weekDays.isEmpty()){
                scheduleDetailViewModel.insertWeekDays(WeekDayData.setWeekDayData())
            }
            Log.d(TAG, "WeekDay Table Exists")
        })
    }

    private fun checkCurrentDay(){
        val calendar = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val days = listOf<MaterialCardView>(binding.scheduleSunday, binding.scheduleMonday, binding.scheduleTuesday, binding.scheduleWednesday, binding.scheduleThursday, binding.scheduleFriday, binding.scheduleSaturday)
        val dayTitles = listOf<TextView>(binding.sundayTitle, binding.mondayTitle, binding.tuesdayTitle, binding.wednesdayTitle, binding.thursdayTitle, binding.fridayTitle, binding.saturdayTitle)
        val dayActivities = listOf<TextView>(binding.sundayActivity, binding.mondayActivity, binding.tuesdayActivity, binding.wednesdayActivity, binding.thursdayActivity, binding.fridayActivity, binding.saturdayActivity)
        val white = resources.getColor(R.color.white)
        val orange = resources.getColor(R.color.orange)
        val black = resources.getColor(R.color.black)

        val daysCardColor = mutableListOf<Int>(white, white, white, white, white, white, white)
        val titleColor = mutableListOf<Int>(black, black, black, black, black, black, black)
        val activityColor = mutableListOf<Int>(black, black, black, black, black, black, black)
        when(calendar){
            1 -> {
                daysCardColor[0] = orange
                titleColor[0] = white
                activityColor[0] = white
            }
            2 -> {
                daysCardColor[1] = orange
                titleColor[1] = white
                activityColor[1] = white
            }
            3 -> {
                daysCardColor[2] = orange
                titleColor[2] = white
                activityColor[2] = white
            }
            4 -> {
                daysCardColor[3] = orange
                titleColor[3] = white
                activityColor[3] = white
            }
            5 -> {
                daysCardColor[4] = orange
                titleColor[4] = white
                activityColor[4] = white
            }
            6 -> {
                daysCardColor[5] = orange
                titleColor[5] = white
                activityColor[5] = white
            }
            7 -> {
                daysCardColor[6] = orange
                titleColor[6] = white
                activityColor[6] = white
            }
        }
        setDayBackgroundColor(days, daysCardColor, dayTitles, titleColor, dayActivities, activityColor)
    }

    private fun setButtonClickListener(){
        binding.scheduleSunday.setOnClickListener { intent(1) }
        binding.scheduleMonday.setOnClickListener { intent(2) }
        binding.scheduleTuesday.setOnClickListener { intent(3) }
        binding.scheduleWednesday.setOnClickListener { intent(4) }
        binding.scheduleThursday.setOnClickListener { intent(5) }
        binding.scheduleFriday.setOnClickListener { intent(6) }
        binding.scheduleSaturday.setOnClickListener { intent(7) }
    }

    private fun setDayBackgroundColor(view: List<MaterialCardView>, color: List<Int>, dayTitle: List<TextView>,titleColor: List<Int>, dayActivity: List<TextView>, activityColor: List<Int>){
        for (i in 0..6){
            view[i].setCardBackgroundColor(color[i])
            dayTitle[i].setTextColor(titleColor[i])
            dayActivity[i].setTextColor(activityColor[i])
        }

    }

    private fun getDayActivityCount(){
        scheduleDetailViewModel.getSchedules().observe(this, { schedule ->
            for(i in 1..7){
                val count = schedule.filter { it.dayId == i.toLong() }
                setDayActivityTextCount(i, count.size)
            }
        })
    }


    private fun setDayActivityTextCount(dayId: Int, scheduleCount: Int){
        val dayActivities = listOf<TextView>(binding.sundayActivity, binding.mondayActivity, binding.tuesdayActivity, binding.wednesdayActivity, binding.thursdayActivity, binding.fridayActivity, binding.saturdayActivity)
        for(i in 0..6){
            dayActivities[dayId-1].text = "$scheduleCount Activity"
        }
    }

    private fun intent(dayId : Long){
        val intentDay = Intent(this, ScheduleDayActivity::class.java)
        intentDay.putExtra(ScheduleDayActivity.EXTRA_DAY_ID, dayId)
        startActivity(intentDay)
    }
}