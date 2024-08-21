package com.polibatam.synchedule.ui.calendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.polibatam.synchedule.R
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.databinding.ActivityCalendarManageTodayBinding
import com.polibatam.synchedule.receiver.AlarmReceiver
import com.polibatam.synchedule.receiver.DeadlineReceiver
import com.polibatam.synchedule.utils.ActivityCodes
import com.polibatam.synchedule.utils.AlarmCodes
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CalendarManageTodayActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CalendarManageActivity"
        const val TYPE = "EXTRA_ACTIVITY_TYPE"
        const val EXTRA_DEADLINE_ID = "EXTRA_DEADLINE_ID"
        const val EXTRA_SUBJECT_ID = "EXTRA_SUBJECT_ID"
    }

    private var _binding : ActivityCalendarManageTodayBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel by viewModels<CalendarViewModel>()

    private val remindList = listOf("No Reminders", "1 Hour Before", "5 Hours Before", "1 Day Before", "2 Days Before")
    private var alarmManager : AlarmManager? = null

    private var subjectId : Long = 0
    private var deadlineId : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCalendarManageTodayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActivityType()
        setTextListener()
        setButtonClickListener()
    }

    private fun initAppBar(type: String?){
        if(type == ActivityCodes.ACTIVITY_ADD){
            binding.appBarLayout.topAppBar.title = "New Activity"
        }

        if(type == ActivityCodes.ACTIVITY_EDIT){
            binding.appBarLayout.topAppBar.title = "Edit Activity"
        }

        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun getActivityType(){
        alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val type = intent.getStringExtra(TYPE)
        if(type == ActivityCodes.ACTIVITY_EDIT){
            deadlineId = intent.getLongExtra(EXTRA_DEADLINE_ID, 0)
            calendarViewModel.deadlineId.value = deadlineId
            subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0)
            getDeadlineData()
        }

        if(type == ActivityCodes.ACTIVITY_ADD){
            initData()
        }

        initAppBar(type)
        initView(type)

    }

    private fun initData(){
        binding.calendarDeadlineTitle.editText?.setText(calendarViewModel.title)
        binding.calendarDeadlineNote.editText?.setText(calendarViewModel.note)
        setDeadlineDate(calendarViewModel.date)
        setDeadlineTime(calendarViewModel.time.toInt())
        getSubjectsData()
        setReminderList()
        setSubjectsData()
    }

    private fun getDeadlineData(){
        calendarViewModel.getDeadlineById(deadlineId).observe(this, { deadline ->
            if(deadline != null){
                calendarViewModel.title = deadline.deadlineTitle
                calendarViewModel.note = deadline.deadlineNote
                calendarViewModel.dateString = deadline.deadlineDate
                calendarViewModel.date = deadline.deadlineTime
                calendarViewModel.time = deadline.deadlineTime
                calendarViewModel.selectedReminder = returnIndexFromRemind((deadline.deadlineTime.toInt() - deadline.deadlineReminder.toInt()))
                initEditData()
            }
        })
    }

    private fun initEditData(){
        binding.calendarDeadlineTitle.editText?.setText(calendarViewModel.title)
        binding.calendarDeadlineNote.editText?.setText(calendarViewModel.note)
        getTimeFromDeadline(calendarViewModel.time)
        setDeadlineDate(calendarViewModel.date)
        getSubjectsData()
        setReminderList()
    }

    private fun initView(type: String?){
        if(type == ActivityCodes.ACTIVITY_ADD){
            binding.calendarDeadlineDelete.visibility = View.GONE
            binding.calendarDeadlineSave.visibility = View.GONE
        }

        if(type == ActivityCodes.ACTIVITY_EDIT){
            binding.calendarDeadlineDelete.visibility = View.VISIBLE
            binding.calendarDeadlineSave.visibility = View.VISIBLE
            binding.calendarDeadlineAdd.visibility = View.GONE
        }
    }

    private fun setButtonClickListener(){
        binding.calendarDeadlineDate.setEndIconOnClickListener { datePicker() }
        binding.calendarDeadlineDate.setOnClickListener { datePicker() }

        binding.calendarDeadlineTime.setEndIconOnClickListener { timePicker() }
        binding.calendarDeadlineTime.setOnClickListener { timePicker() }

        binding.calendarDeadlineAdd.setOnClickListener { addData() }

        binding.calendarDeadlineSave.setOnClickListener { saveData() }

        binding.calendarDeadlineDelete.setOnClickListener {
            deleteDeadline()
        }
    }

    private fun setTextListener(){
        binding.calendarDeadlineTitle.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calendarViewModel.title = s?.toString().toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.calendarDeadlineNote.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calendarViewModel.note = s?.toString().toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getSubjectsData(){
        calendarViewModel.getSubjects().observe(this, { data ->
            calendarViewModel.subjectsData.clear()
            calendarViewModel.subjectFullData.clear()
            data.forEach {
                calendarViewModel.subjectsData.add(it.subjectName)
            }
            calendarViewModel.subjectFullData.addAll(data)
            if(calendarViewModel.subjectFullData.isNotEmpty()){
                setSubjectsData()
            }
        })
    }

    private fun datePicker(){
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Deadline Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(supportFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            calendarViewModel.date = it
            setDeadlineDate(it)
        }
    }

    private fun timePicker(){
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Select Deadline Time")
            .build()

        timePicker.show(supportFragmentManager, "TimePicker")
        timePicker.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            val hourInSecond = timePicker.hour * 3600
            val minuteInSecond = timePicker.minute * 60
            val timeInSeconds = (hourInSecond + minuteInSecond)
            setDeadlineTime(timeInSeconds)
            calendarViewModel.time = calendar.timeInMillis
        }
    }

    private fun setReminderList(){
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, remindList)
        (binding.calendarDeadlineRemindTime.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.calendarDeadlineReminderDropdown.setOnItemClickListener { _, _, position, _ ->
            calendarViewModel.selectedReminder = position
            Log.d(TAG, "Position : $position")
        }
        binding.calendarDeadlineReminderDropdown.setText(remindList[calendarViewModel.selectedReminder], false)
        binding.calendarDeadlineReminderDropdown.setSelection(calendarViewModel.selectedReminder)
    }

    private fun setSubjectsData(){
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, calendarViewModel.subjectsData)
        (binding.calendarDeadlineSubject.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.calendarDeadlineSubjectDropdown.setOnItemClickListener { _, _, position, _ ->
            calendarViewModel.selectedSubjectIndex = position
        }

        if(subjectId.toInt() != 0){
            val selectedSubject = calendarViewModel.subjectFullData.find { it.id?.equals(subjectId) == true }
            calendarViewModel.selectedSubjectIndex = calendarViewModel.subjectFullData.indexOf(selectedSubject)

            binding.calendarDeadlineSubjectDropdown.setText(calendarViewModel.subjectsData[calendarViewModel.selectedSubjectIndex].toString(), false)
            binding.calendarDeadlineSubjectDropdown.setSelection(calendarViewModel.selectedSubjectIndex)
        }

        if(subjectId.toInt() == 0 && calendarViewModel.subjectsData.isNotEmpty()){
            binding.calendarDeadlineSubjectDropdown.setText(calendarViewModel.subjectsData[calendarViewModel.selectedSubjectIndex].toString(), false)
            binding.calendarDeadlineSubjectDropdown.setSelection(calendarViewModel.selectedSubjectIndex)
        }
    }

    private fun setDeadlineDate(dateLong : Long){
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = dateLong

        val sdf = SimpleDateFormat("dd LLL yyyy", Locale.getDefault()).format(calendar.time)
        calendarViewModel.dateString = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        binding.calendarDeadlineDate.editText?.setText(sdf.toString())
    }

    private fun setDeadlineTime(time: Int){
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.set(Calendar.HOUR_OF_DAY, (time / 3600))
        calendar.set(Calendar.MINUTE, ((time % 3600) / 60))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        binding.calendarDeadlineTime.editText?.setText(sdf.toString())
    }

    private fun addData(){
        if(binding.calendarDeadlineTitle.editText?.text.isNullOrEmpty()){
            binding.calendarDeadlineTitle.editText?.error = "Title Is Required"
            return
        }

        if(binding.calendarDeadlineNote.editText?.text.isNullOrEmpty()){
            binding.calendarDeadlineNote.editText?.error = "Note Is Required"
            return
        }

        if(calendarViewModel.selectedSubjectIndex == 0 && calendarViewModel.subjectFullData.isEmpty()){
            Toast.makeText(this, "Please Add Subject.", Toast.LENGTH_SHORT).show()
            return
        }

        calendarViewModel.addDeadline(
            DeadlineEntity(
                null,
                calendarViewModel.subjectFullData[calendarViewModel.selectedSubjectIndex].id!!,
                calendarViewModel.title,
                calendarViewModel.dateString,
                returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time),
                (returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time) - returnRemindSeconds(calendarViewModel.selectedReminder)),
                calendarViewModel.note
            )
        )

        calendarViewModel.deadlineId.observe(this, { deadlineId ->
            if(deadlineId.toInt() != 0){
                cancelAlarm(deadlineId)
                setAlarm(deadlineId, calendarViewModel.time, (returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time) - returnRemindSeconds(calendarViewModel.selectedReminder)), calendarViewModel.title)
            }
        })
    }

    private fun saveData(){
        if(binding.calendarDeadlineTitle.editText?.text.isNullOrEmpty()){
            binding.calendarDeadlineTitle.editText?.error = "Title Is Required"
            return
        }

        if(binding.calendarDeadlineNote.editText?.text.isNullOrEmpty()){
            binding.calendarDeadlineNote.editText?.error = "Note Is Required"
            return
        }

        calendarViewModel.updateDeadline(
            DeadlineEntity(
                deadlineId,
                calendarViewModel.subjectFullData[calendarViewModel.selectedSubjectIndex].id!!,
                calendarViewModel.title,
                calendarViewModel.dateString,
                returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time),
                (returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time) - returnRemindSeconds(calendarViewModel.selectedReminder)),
                calendarViewModel.note
            )
        )

        cancelAlarm(deadlineId)
        setAlarm(deadlineId, calendarViewModel.time, (returnDeadlineTime(calendarViewModel.dateString, calendarViewModel.time) - returnRemindSeconds(calendarViewModel.selectedReminder)), calendarViewModel.title)


    }

    private fun deleteDeadline(){
        AlertDialog.Builder(this)
            .setMessage("Do You Want To Delete This Schedule ?")
            .setCancelable(false)
            .setPositiveButton("Yes") {_, _ ->
                cancelAlarm(deadlineId)
                calendarViewModel.deleteDeadlineById(deadlineId)
                finish()
            }
            .setNegativeButton("No") {dialogue, id ->
                dialogue.dismiss()
            }
            .create()
            .show()
    }

    private fun getTimeFromDeadline(deadline: Long){
        val time = Calendar.getInstance()
        time.timeZone = TimeZone.getDefault()
        time.timeInMillis = deadline

        val hourInSecond = time.get(Calendar.HOUR_OF_DAY) * 3600
        val minuteInSecond = time.get(Calendar.MINUTE) * 60
        val timeInSeconds = (hourInSecond + minuteInSecond)
        setDeadlineTime(timeInSeconds)
        calendarViewModel.time = time.timeInMillis
    }

    private fun returnRemindSeconds(index: Int) : Long{
       return when(index){
            0 -> 0
            1 -> 3600000
            2 -> 18000000
            3 -> 86400000
            4 -> 172800000
           else -> 0
       }
    }

    private fun returnIndexFromRemind(time: Int) : Int{
        return when(time){
            0 -> 0
            3600000 -> 1
            18000000 -> 2
            86400000 -> 3
            172800000 -> 4
            else -> 0
        }
    }

    private fun returnDeadlineTime(day: String, deadline: Long) : Long{
        val timeCalendar = Calendar.getInstance(Locale.getDefault())
        timeCalendar.timeZone = TimeZone.getDefault()
        timeCalendar.timeInMillis = deadline

        val dateSplit = day.split("-")

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeZone = TimeZone.getDefault()
        calendar.set(Calendar.DAY_OF_MONTH, dateSplit[0].toInt())
        calendar.set(Calendar.MONTH, (dateSplit[1].toInt()-1))
        calendar.set(Calendar.YEAR, dateSplit[2].toInt())
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    private fun setAlarm(deadlineId : Long, deadlineTime: Long ,remindTime: Long, title: String){
        val calendarDeadline = Calendar.getInstance()
        calendarDeadline.timeZone = TimeZone.getDefault()
        calendarDeadline.timeInMillis = deadlineTime
        val deadline = SimpleDateFormat("hh:mm", Locale.getDefault()).format(calendarDeadline.time)

        Log.d(TAG, "Remind Time $remindTime")
        Log.d(TAG, "Deadline ID : $deadlineId")

        val pendingIntent by lazy {
            val intentDeadline = Intent(this, DeadlineReceiver::class.java)
            intentDeadline.putExtra(AlarmCodes.EXTRA_SCHEDULE_DEADLINE_TITLE, title)
            intentDeadline.putExtra(AlarmCodes.EXTRA_SCHEDULE_DEADLINE_ID, deadlineId.toInt())
            intentDeadline.putExtra(AlarmCodes.EXTRA_SCHEDULE_DEADLINE_TIME, deadline)
            PendingIntent.getBroadcast(this, deadlineId.toInt(), intentDeadline, 0)
        }
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    remindTime,
                    pendingIntent
                )
            }
            else -> {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, remindTime, pendingIntent)
            }
        }
        finish()
    }

    private fun cancelAlarm(id: Long){
        val pendingIntent by lazy {
            val intent = Intent(this, AlarmReceiver::class.java)
            PendingIntent.getBroadcast(this, id.toInt(), intent, 0)
        }
        alarmManager?.cancel(pendingIntent)
    }

    override fun onPause() {
        super.onPause()
        binding.calendarDeadlineReminderDropdown.setText("", false)
        binding.calendarDeadlineSubjectDropdown.setText("", false)
    }

    override fun onResume() {
        super.onResume()
        binding.calendarDeadlineReminderDropdown.setText(remindList[calendarViewModel.selectedReminder], false)
        binding.calendarDeadlineReminderDropdown.setSelection(calendarViewModel.selectedReminder)

        if(calendarViewModel.subjectFullData.size != 0){
            binding.calendarDeadlineSubjectDropdown.setText(calendarViewModel.subjectsData[calendarViewModel.selectedSubjectIndex].toString(), false)
            binding.calendarDeadlineSubjectDropdown.setSelection(calendarViewModel.selectedSubjectIndex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ViewDestroyed")
    }
}