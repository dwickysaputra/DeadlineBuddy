package com.polibatam.synchedule.ui.schedule.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.polibatam.synchedule.data.entity.relation.ScheduleAndSubject
import com.polibatam.synchedule.databinding.ActivityScheduleDayBinding
import com.polibatam.synchedule.utils.ActivityCodes
import com.polibatam.synchedule.utils.WeekDayData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleDayActivity : AppCompatActivity(){

    companion object {
        const val EXTRA_DAY_ID = "extra_day_id"
        private const val TAG = "ScheduleDayActivity"
    }

    private var _binding : ActivityScheduleDayBinding? = null
    private val binding get() = _binding!!
    private var dayId : Long = 0

    private val scheduleDetailViewModel by viewModels<ScheduleDetailViewModel>()
    @Inject lateinit var scheduleDayRecycleViewAdapter: ScheduleDayRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleDayBinding.inflate(layoutInflater)
        initAppBar()
        setRecyclerView()
        getSchedules()
        setContentView(binding.root)

        setButtonClickListener()
    }

    private fun initAppBar(){
        dayId = intent.getLongExtra(EXTRA_DAY_ID, 0)
        binding.appBarLayout.topAppBar.title = WeekDayData.returnDayName(dayId)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun setButtonClickListener(){
        binding.addActivityFab.setOnClickListener {
            intentManageDay(ActivityCodes.ACTIVITY_ADD, null, null)
        }
        scheduleDayRecycleViewAdapter.setOnItemClickCallback(object : ScheduleDayRecycleViewAdapter.OnItemScheduleClickCallback{
            override fun onItemScheduleClicked(scheduleAndSubject: ScheduleAndSubject) {
                intentManageDay(ActivityCodes.ACTIVITY_EDIT, scheduleAndSubject.scheduleEntity.scheduleId, scheduleAndSubject.scheduleEntity.subjectId)
            }
        })
    }

    private fun setRecyclerView(){
        binding.scheduleDayRv.apply {
            layoutManager = LinearLayoutManager(this@ScheduleDayActivity)
            adapter = scheduleDayRecycleViewAdapter
            setHasFixedSize(false)
        }
    }

    private fun getSchedules(){
        scheduleDetailViewModel.getSubjectAndSchedules(dayId).observe(this, {data ->
            binding.scheduleCount.text = "${data.size} Activity"
            scheduleDayRecycleViewAdapter.setSchedules(data)
            scheduleDayRecycleViewAdapter.notifyDataSetChanged()
        })

    }

    private fun intentManageDay(type: String, scheduleId : Long?, subjectId: Long?){
        val intentDay = Intent(this, ScheduleManageDayActivity::class.java)
        intentDay.putExtra(ScheduleManageDayActivity.EXTRA_DAY_ID, dayId)
        intentDay.putExtra(ScheduleManageDayActivity.EXTRA_MANAGE_TYPE, type)
        intentDay.putExtra(ScheduleManageDayActivity.EXTRA_SCHEDULE_ID, scheduleId)
        intentDay.putExtra(ScheduleManageDayActivity.EXTRA_SUBJECT_ID, subjectId)
        startActivity(intentDay)
    }

}