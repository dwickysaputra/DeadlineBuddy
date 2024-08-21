package com.polibatam.synchedule.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.polibatam.synchedule.R
import com.polibatam.synchedule.databinding.ActivityMainBinding
import com.polibatam.synchedule.ui.calendar.CalendarActivity
import com.polibatam.synchedule.ui.deadline.DeadlineActivity
import com.polibatam.synchedule.ui.settings.SettingsActivity
import com.polibatam.synchedule.ui.schedule.ScheduleMainActivity
import com.polibatam.synchedule.utils.CalendarUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel>()
    @Inject lateinit var mainActivityRecyclerView: MainActivityRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Synchedule)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getProfileData()
        setCurrentCalendar()
        setClickListener()
        getScheduleByDay()
    }



    private fun getScheduleByDay(){
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        mainViewModel.getScheduleSubject(day.toLong()).observe(this, { schedules ->
            mainActivityRecyclerView.setScheduleData(schedules)
        })
    }

    private fun setCurrentCalendar(){
        val sdf = SimpleDateFormat("dd.LLLL yyyy.EEEE",Locale.getDefault()).format(Calendar.getInstance().time)
        val dateFormat = sdf.split(".")
        binding.mainDateText.text = dateFormat[0]
        binding.mainMonthYearText.text = dateFormat[1]
        binding.mainDayText.text = dateFormat[2]
    }

    private fun getProfileData(){
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_profile), Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString(getString(R.string.shared_pref_profile_first_name), "Your")
        val lastName = sharedPreferences.getString(getString(R.string.shared_pref_profile_last_name), "Name.")
        val profilePic = sharedPreferences.getString(getString(R.string.shared_pref_profile_pic), null)
        if(profilePic == null){
            binding.mainProfilePic.setImageResource(R.drawable.ic_user_pic)
        }else {
            binding.mainProfilePic.setImageURI(Uri.parse(profilePic))
        }

        binding.mainNameText.text = "$firstName $lastName"
    }

    private fun setClickListener(){
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.itemSettings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    binding.drawerLayout.closeDrawers()
                    false
                }
                R.id.itemSchedule -> {
                    startActivity(Intent(this, ScheduleMainActivity::class.java))
                    binding.drawerLayout.closeDrawers()
                    false
                }
                R.id.itemCalendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    binding.drawerLayout.closeDrawers()
                    false
                }
                R.id.itemDeadline -> {
                    startActivity(Intent(this, DeadlineActivity::class.java))
                    binding.drawerLayout.closeDrawers()
                    false
                }
                else -> false
            }
        }

        binding.mainScheduleMenu.setOnClickListener { startActivity(Intent(this, ScheduleMainActivity::class.java)) }
        binding.mainCalendarMenu.setOnClickListener { startActivity(Intent(this, CalendarActivity::class.java)) }
        binding.mainDeadlineMenu.setOnClickListener { startActivity(Intent(this, DeadlineActivity::class.java)) }
    }


    override fun onResume() {
        super.onResume()
        getProfileData()
    }
}