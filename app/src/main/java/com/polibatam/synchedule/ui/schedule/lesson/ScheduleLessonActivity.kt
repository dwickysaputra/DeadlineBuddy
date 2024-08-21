package com.polibatam.synchedule.ui.schedule.lesson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.polibatam.synchedule.R
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.databinding.ActivityScheduleLessonBinding
import com.polibatam.synchedule.utils.ActivityCodes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleLessonActivity : AppCompatActivity(){

    companion object {
        private const val TAG = "ScheduleLessonActivity"
    }

    private var _binding : ActivityScheduleLessonBinding? = null
    private val binding get() = _binding!!
    private var resultIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        Log.d(TAG, "Data : $result")
    }
    private val scheduleLessonViewModel by viewModels<ScheduleLessonViewModel>()
    @Inject lateinit var scheduleLessonAdapter: ScheduleLessonRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleLessonBinding.inflate(layoutInflater)
        initAppbar()
        setContentView(binding.root)

        setClickListener()
        showRecyclerList()
        setRecyclerViewData()
    }

    private fun initAppbar(){
        binding.appBarLayout.topAppBar.title = getString(R.string.schedule_lessons_title)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showRecyclerList(){
        binding.lessonRv.layoutManager = LinearLayoutManager(this)
        binding.lessonRv.adapter = scheduleLessonAdapter
        binding.lessonRv.setHasFixedSize(false)
    }

    private fun setRecyclerViewData(){
        scheduleLessonViewModel.lessons.observe(this, { data ->
            scheduleLessonAdapter.setLessonData(data)
            scheduleLessonAdapter.notifyDataSetChanged()
            binding.lessonCount.text = "${data.size} Lessons"
        })
    }

    private fun setClickListener(){
        binding.addLessonFab.setOnClickListener {
            intent(ActivityCodes.ACTIVITY_ADD, 0)
        }
        scheduleLessonAdapter.setOnItemLessonClickCallback(object : ScheduleLessonRecyclerViewAdapter.OnItemLessonClickCallback{
            override fun onItemLessonClicked(lesson: SubjectEntity) {
                intent(ActivityCodes.ACTIVITY_EDIT, lesson.id)
            }
        })
    }

    private fun intent(type: String, id: Long?){
        val manageLessonIntent = Intent(this, ScheduleLessonManageActivity::class.java)
        manageLessonIntent.putExtra(ScheduleLessonManageActivity.EXTRA_TYPE, type)
        manageLessonIntent.putExtra(ScheduleLessonManageActivity.EXTRA_SUBJECT_ID, id)
        resultIntent.launch(manageLessonIntent)
    }

}