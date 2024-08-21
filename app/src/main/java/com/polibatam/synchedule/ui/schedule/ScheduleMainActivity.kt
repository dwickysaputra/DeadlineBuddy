package com.polibatam.synchedule.ui.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.polibatam.synchedule.R
import com.polibatam.synchedule.databinding.ActivityScheduleMainBinding
import com.polibatam.synchedule.ui.schedule.detail.ScheduleDetailActivity
import com.polibatam.synchedule.ui.schedule.lesson.ScheduleLessonActivity

class ScheduleMainActivity : AppCompatActivity() {
    private var _binding : ActivityScheduleMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleMainBinding.inflate(layoutInflater)
        initAppBar()
        setContentView(binding.root)

        setClickListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.title = getString(R.string.schedule_main_title)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setClickListener(){
        binding.scheduleLessonBtn.setOnClickListener { startActivity(Intent(this, ScheduleLessonActivity::class.java)) }
        binding.scheduleDetailBtn.setOnClickListener { startActivity(Intent(this, ScheduleDetailActivity::class.java)) }
    }
}