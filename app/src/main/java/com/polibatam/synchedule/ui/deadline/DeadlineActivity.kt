package com.polibatam.synchedule.ui.deadline

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.polibatam.synchedule.R
import com.polibatam.synchedule.data.entity.DeadlineEntity
import com.polibatam.synchedule.data.entity.relation.DeadlineAndSubject
import com.polibatam.synchedule.databinding.ActivityDeadlineBinding
import com.polibatam.synchedule.receiver.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeadlineActivity : AppCompatActivity() {

    private var _binding : ActivityDeadlineBinding? = null
    private val binding get() = _binding!!

    private val deadlineSortList = listOf<String>("All", "Completed", "Incomplete")
    private val deadlineViewModel by viewModels<DeadlineViewModel>()

    @Inject lateinit var deadlineActivityRecyclerView: DeadlineActivityRecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeadlineBinding.inflate(layoutInflater)
        initAppBar()
        setContentView(binding.root)
        setSortList()
        setRecyclerView()
        getDeadlinesData()
        setMenuItemListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.title = "Deadlines"
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun setSortList(){
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown, deadlineSortList)
        (binding.deadlineFilter.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.deadlineFilterDropdown.setOnItemClickListener { _, _, position, _ ->
            deadlineViewModel.selectedReminderIndex = position
            getDeadlinesData()
        }
        binding.deadlineFilterDropdown.setSelection(deadlineViewModel.selectedReminderIndex)
        binding.deadlineFilterDropdown.setText(deadlineSortList[deadlineViewModel.selectedReminderIndex], false)
    }

    private fun setRecyclerView(){
        binding.deadlineRv.apply {
            layoutManager = LinearLayoutManager(this@DeadlineActivity)
            adapter = deadlineActivityRecyclerView
            setHasFixedSize(false)
        }
    }

    private fun setMenuItemListener(){
        deadlineActivityRecyclerView.setOnDeadlineMenuClickCallback(object: DeadlineActivityRecyclerView.OnDeadlineMenuClickCallback{
            override fun setOnDeadlineMenuItemClicked(deadlineAndSubject: DeadlineAndSubject, view: View) {
                openOptionMenu(view, deadlineAndSubject)
            }
        })
    }

    private fun openOptionMenu(view: View, deadlineAndSubject: DeadlineAndSubject){
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_deadline, popupMenu.menu)

        popupMenu.menu.findItem(R.id.menuComplete).isVisible = !deadlineAndSubject.deadlineEntity.deadlineStatus

        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menu ->
            when(menu.itemId){
                R.id.menuComplete -> {
                    completeDeadlineDialog(deadlineAndSubject.deadlineEntity)
                    return@setOnMenuItemClickListener true
                }
                R.id.menuDelete -> {
                    deleteDeadlineDialog(deadlineAndSubject.deadlineEntity.deadlineId!!)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener true
            }

        }
    }

    private fun completeDeadlineDialog(deadlineEntity: DeadlineEntity){
        AlertDialog.Builder(this)
            .setMessage("Do You Want To Set This Deadline To Completed ?")
            .setCancelable(false)
            .setPositiveButton("Yes") {_, _ ->
                setDeadlineComplete(deadlineEntity)
            }
            .setNegativeButton("No") {dialogue, _ ->
                dialogue.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteDeadlineDialog(deadlineId: Long){
        AlertDialog.Builder(this)
            .setMessage("Do You Want To Delete This Deadline ?")
            .setCancelable(false)
            .setPositiveButton("Yes") {_, _ ->
                deleteDeadline(deadlineId)
            }
            .setNegativeButton("No") {dialogue, _ ->
                dialogue.dismiss()
            }
            .create()
            .show()
    }

    private fun getDeadlinesData(){
        when(deadlineViewModel.selectedReminderIndex){
            0 -> {
                deadlineViewModel.getAllLatestDeadline().observe(this, { deadlines ->
                    deadlineActivityRecyclerView.setDeadlineData(deadlines)
                    deadlineActivityRecyclerView.notifyDataSetChanged()
                })
            }
            1 -> {
                deadlineViewModel.getCompletedDeadline().observe(this, { deadlines ->
                    deadlineActivityRecyclerView.setDeadlineData(deadlines)
                    deadlineActivityRecyclerView.notifyDataSetChanged()
                })
            }
            2 -> {
                deadlineViewModel.getInCompleteDeadline().observe(this, { deadlines ->
                    deadlineActivityRecyclerView.setDeadlineData(deadlines)
                    deadlineActivityRecyclerView.notifyDataSetChanged()
                })
            }
        }

    }

    private fun setDeadlineComplete(deadlineEntity: DeadlineEntity){
        deadlineViewModel.setDeadlineComplete(
            DeadlineEntity(
                deadlineEntity.deadlineId,
                deadlineEntity.subjectId,
                deadlineEntity.deadlineTitle,
                deadlineEntity.deadlineDate,
                deadlineEntity.deadlineTime,
                deadlineEntity.deadlineReminder,
                deadlineEntity.deadlineNote,
                true
            )
        )
        cancelAlarm(deadlineEntity.deadlineId!!)
    }

    private fun deleteDeadline(deadlineId: Long){
        deadlineViewModel.deleteDeadline(deadlineId)
        cancelAlarm(deadlineId)
    }

    private fun cancelAlarm(id: Long){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent by lazy {
            val intent = Intent(this, AlarmReceiver::class.java)
            PendingIntent.getBroadcast(this, id.toInt(), intent, 0)
        }
        alarmManager?.cancel(pendingIntent)
    }
}