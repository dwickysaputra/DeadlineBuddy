package com.polibatam.synchedule.ui.schedule.detail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.polibatam.synchedule.R
import com.polibatam.synchedule.data.entity.ScheduleEntity
import com.polibatam.synchedule.databinding.ActivityScheduleManageDayBinding
import com.polibatam.synchedule.receiver.AlarmReceiver
import com.polibatam.synchedule.utils.ActivityCodes
import com.polibatam.synchedule.utils.AlarmCodes
import com.polibatam.synchedule.utils.WeekDayData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import java.util.*

@AndroidEntryPoint
class ScheduleManageDayActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScheduleMngDayActivity"
        const val EXTRA_MANAGE_TYPE = "EXTRA_MANAGE_TYPE"
        const val EXTRA_DAY_ID = "EXTRA_DAY_ID"
        const val EXTRA_SCHEDULE_ID = "EXTRA_SCHEDULE_ID"
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
    }

    private var _binding: ActivityScheduleManageDayBinding? = null
    private val binding get() = _binding!!

    private var dayId : Long = 0
    private var scheduleId : Long = 0
    private var subjectId : Long = 0

    private val scheduleDetailViewModel by viewModels<ScheduleDetailViewModel>()

    private var alarmManager : AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleManageDayBinding.inflate(layoutInflater)
        initAppBar()
        setContentView(binding.root)
        getActivityType()
        setButtonClickListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun getActivityType(){
        dayId = intent.getLongExtra(EXTRA_DAY_ID, 0)
        val type = intent.getStringExtra(EXTRA_MANAGE_TYPE)

        if(type == ActivityCodes.ACTIVITY_ADD){
            binding.appBarLayout.topAppBar.title = "Add Lesson"
            binding.manageDayTitle.text = WeekDayData.returnDayName(dayId)
            binding.manageDayDeleteBtn.visibility = View.GONE
            binding.manageDaySaveBtn.visibility = View.GONE
            initData(type)
        }

        if(type == ActivityCodes.ACTIVITY_EDIT){
            binding.manageDayAddBtn.visibility = View.GONE
            scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, 0)
            subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0)
            scheduleDetailViewModel.scheduleId.value = scheduleId
            binding.appBarLayout.topAppBar.title = "Manage Lesson"
            binding.manageDayTitle.text = WeekDayData.returnDayName(dayId)
            initData(type)
        }
    }

    private fun initData(type: String){
        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        if(type == ActivityCodes.ACTIVITY_ADD){
            binding.manageStartTimeText.text = scheduleDetailViewModel.startTime
            binding.manageEndTimeText.text = scheduleDetailViewModel.endTime
            binding.manageRemind.isChecked = scheduleDetailViewModel.remind
        }

        if(type == ActivityCodes.ACTIVITY_EDIT){
            scheduleDetailViewModel.getScheduleAndSubjectById(scheduleId).observe(this, { data ->
                if(data == null){
                    scheduleDetailViewModel.startTime = setCleanTime(0)
                    scheduleDetailViewModel.endTime = setCleanTime(0)
                }

                if(data !== null){
                    scheduleDetailViewModel.startTime = setCleanTime(data.scheduleEntity.startTime)
                    scheduleDetailViewModel.endTime = setCleanTime(data.scheduleEntity.endTime)
                    scheduleDetailViewModel.remind = data.scheduleEntity.remind

                    binding.manageStartTimeText.text = scheduleDetailViewModel.startTime
                    binding.manageEndTimeText.text = scheduleDetailViewModel.endTime
                    binding.manageRemind.isChecked = scheduleDetailViewModel.remind
                }

            })
        }

        getSubjectData()
    }

    private fun getSubjectData(){
        scheduleDetailViewModel.getSubjects().observe(this, { data ->
            scheduleDetailViewModel.subjectsData.clear()
            scheduleDetailViewModel.subjectFullData.clear()
            data.forEach {
                scheduleDetailViewModel.subjectsData.add(it.subjectName)
            }
            scheduleDetailViewModel.subjectFullData.addAll(data)

            if(scheduleDetailViewModel.subjectFullData.isNotEmpty()){
                setSubjectsData()
            }
        })

    }

    private fun setSubjectsData(){
        val adapter = ArrayAdapter(applicationContext, R.layout.list_item_dropdown, scheduleDetailViewModel.subjectsData)
        (binding.manageDayLessonPick.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.manageDayLessonText.setOnItemClickListener { _, _, position, _ ->
            scheduleDetailViewModel.selectedSubjectIndex = position
        }
        if(subjectId.toInt() != 0){
            val selectedSubject = scheduleDetailViewModel.subjectFullData.find { it.id?.equals(subjectId) == true }
            scheduleDetailViewModel.selectedSubjectIndex = scheduleDetailViewModel.subjectFullData.indexOf(selectedSubject)

            binding.manageDayLessonText.setText(scheduleDetailViewModel.subjectsData[scheduleDetailViewModel.selectedSubjectIndex], false)
            binding.manageDayLessonText.setSelection(scheduleDetailViewModel.selectedSubjectIndex)
        }

    }

    private fun setButtonClickListener(){
        binding.manageTimeStart.setOnClickListener { timePickerDialogue(binding.manageStartTimeText, "Set Start Time") }
        binding.manageTimeEnd.setOnClickListener { timePickerDialogue(binding.manageEndTimeText, "Set End Time") }
        binding.manageDayAddBtn.setOnClickListener {
            if(binding.manageDayLessonText.text?.isEmpty() == true){
                Toast.makeText(this, "Please Select A Lesson", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addData()
        }
        binding.manageDaySaveBtn.setOnClickListener {
            if(binding.manageDayLessonText.text?.isEmpty() == true){
                Toast.makeText(this, "Please Select A Lesson", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveData()
        }
        binding.manageDayDeleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Do You Want To Delete This Schedule ?")
                .setCancelable(false)
                .setPositiveButton("Yes") {dialogue, id ->
                    deleteData()
                }
                .setNegativeButton("No") {dialogue, id ->
                    dialogue.dismiss()
                }
                .create()
                .show()
        }
        binding.manageRemind.setOnClickListener { scheduleDetailViewModel.remind = binding.manageRemind.isChecked }
    }

    private fun timePickerDialogue(textView: TextView, title: String){
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText(title)
                .build()

        picker.show(supportFragmentManager, "TimePicker")
        picker.addOnPositiveButtonClickListener {
            val hour = if(picker.hour < 10){ "0${picker.hour}" }else { picker.hour }
            val minute = if(picker.minute < 10) {"0${picker.minute}"} else { picker.minute }
            textView.text = "$hour:$minute"
            if(textView == binding.manageStartTimeText){
                scheduleDetailViewModel.startTime = "$hour:$minute"
            }
            if(textView == binding.manageEndTimeText){
                scheduleDetailViewModel.endTime = "$hour:$minute"
            }
        }
    }

    private fun addData(){
        val cleanStartTime = scheduleDetailViewModel.startTime.split(":")
        val cleanEndTime = scheduleDetailViewModel.endTime.split(":")
        val startTime : Long = (cleanStartTime[0].toLong() * 3600) + (cleanStartTime[1].toLong() * 60)
        val endTime : Long = (cleanEndTime[0].toLong() * 3600) + (cleanEndTime[1].toLong() * 60)

        scheduleDetailViewModel.insertSchedule(
            ScheduleEntity(
                null,
                dayId,
                scheduleDetailViewModel.subjectFullData[scheduleDetailViewModel.selectedSubjectIndex].id!!,
                startTime,
                endTime,
                scheduleDetailViewModel.remind
            )
        )

        scheduleDetailViewModel.scheduleId.observe(this, { scheduleId ->
            if(scheduleId.toInt() != 0){
                if(scheduleDetailViewModel.remind){
                    cancelAlarm(scheduleId)
                    setAlarm(scheduleId, startTime, endTime, scheduleDetailViewModel.subjectFullData[scheduleDetailViewModel.selectedSubjectIndex].subjectName)
                }
            }
        })
    }

    private fun saveData(){
        val cleanStartTime = scheduleDetailViewModel.startTime.split(":")
        val cleanEndTime = scheduleDetailViewModel.endTime.split(":")

        val startTime : Long = (cleanStartTime[0].toLong() * 3600) + (cleanStartTime[1].toLong() * 60)
        val endTime : Long = (cleanEndTime[0].toLong() * 3600) + (cleanEndTime[1].toLong() * 60)

        scheduleDetailViewModel.updateSchedule(
            ScheduleEntity(
                scheduleId,
                dayId,
                scheduleDetailViewModel.subjectFullData[scheduleDetailViewModel.selectedSubjectIndex].id!!,
                startTime,
                endTime,
                scheduleDetailViewModel.remind
            )
        )

        scheduleDetailViewModel.scheduleId.observe(this, { scheduleId ->
            if(scheduleDetailViewModel.remind){
                cancelAlarm(scheduleId)
                setAlarm(scheduleId, startTime, endTime, scheduleDetailViewModel.subjectFullData[scheduleDetailViewModel.selectedSubjectIndex].subjectName)
            }else {
                finish()
            }

        })
    }

    private fun deleteData(){
        AlertDialog.Builder(this)
            .setMessage("Do You Want To Delete This Schedule ?")
            .setCancelable(false)
            .setPositiveButton("Yes") {_, _ ->
                cancelAlarm(scheduleId)
                scheduleDetailViewModel.deleteSchedule(scheduleId)
                finish()
            }
            .setNegativeButton("No") {dialogue, id ->
                dialogue.dismiss()
            }
            .create()
            .show()
    }

    private fun setAlarm(scheduleId : Long, startTime : Long, endTime : Long, subjectName : String){
        val remindTime = startTime - 1800
        val hour = remindTime / 3600
        val minute = (remindTime % 3600) / 60

        val startTimeHour = startTime / 3600
        val startTimeMinute = (startTime % 3600) / 60
        val startTimeHourText = if(startTimeHour < 10) { "0${startTimeHour}" } else { "$startTimeHour" }
        val startTimeMinuteText = if(startTimeMinute < 10) { "0${startTimeMinute}" } else { "$startTimeMinute" }

        val endTimeHour = endTime / 3600
        val endTimeMinute = (endTime % 3600) / 60
        val endTimeHourText = if(endTimeHour < 10) { "0${endTimeHour}" } else { "$endTimeHour" }
        val endTimeMinuteText = if(endTimeMinute < 10) { "0${endTimeMinute}" } else { "$endTimeMinute" }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, dayId.toInt())
        calendar.set(Calendar.HOUR_OF_DAY, hour.toInt())
        calendar.set(Calendar.MINUTE, minute.toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val pendingIntent by lazy {
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_TITLE, subjectName)
            intent.putExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_SCHEDULE_ID, scheduleId.toInt())
            intent.putExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_START_TIME, "${startTimeHourText}:${startTimeMinuteText}")
            intent.putExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_END_TIME, "${endTimeHourText}:${endTimeMinuteText}")
            PendingIntent.getBroadcast(this, scheduleId.toInt(), intent, 0)
        }
        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, (7 * 24 * 60 * 60 * 1000), pendingIntent)
        finish()
    }

    private fun cancelAlarm(id: Long){
        val pendingIntent by lazy {
            val intent = Intent(this, AlarmReceiver::class.java)
            PendingIntent.getBroadcast(this, id.toInt(), intent, 0)
        }
        alarmManager?.cancel(pendingIntent)
    }

    private fun setCleanTime(time: Long) : String{
        val startTimeHour = time.div(3600)
        val startTimeMinute = (time.rem(3600))?.div(60)
        val cleanStartTimeHour = if(startTimeHour < 10) { "0${startTimeHour}" } else {"$startTimeHour"}
        val cleanStartTimeMinute = if(startTimeMinute < 10) { "0${startTimeMinute}" } else {"$startTimeMinute"}
        return "$cleanStartTimeHour:$cleanStartTimeMinute"
    }

    override fun onPause() {
        super.onPause()
        binding.manageDayLessonText.setText("", false)
    }

    override fun onResume() {
        super.onResume()
        if(scheduleDetailViewModel.subjectFullData.isNotEmpty() && subjectId.toInt() != 0){
            val selectedSubject = scheduleDetailViewModel.subjectFullData.find { it.id?.equals(subjectId) == true }
            scheduleDetailViewModel.selectedSubjectIndex = scheduleDetailViewModel.subjectFullData.indexOf(selectedSubject)

            binding.manageDayLessonText.setText(scheduleDetailViewModel.subjectsData[scheduleDetailViewModel.selectedSubjectIndex], false)
            binding.manageDayLessonText.setSelection(scheduleDetailViewModel.selectedSubjectIndex)
        }
    }

}