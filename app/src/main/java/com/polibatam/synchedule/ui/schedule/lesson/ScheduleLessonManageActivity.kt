package com.polibatam.synchedule.ui.schedule.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.polibatam.synchedule.databinding.ActivityScheduleLessonManageBinding
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import com.google.android.material.card.MaterialCardView
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.utils.ActivityCodes

import com.skydoves.colorpickerview.ColorEnvelope

import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

import com.skydoves.colorpickerview.ColorPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers


@AndroidEntryPoint
class ScheduleLessonManageActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_SUBJECT_ID = "extra_subject_id"
    }

    private var _binding : ActivityScheduleLessonManageBinding? = null
    private val binding get() = _binding!!
    private val scheduleLessonViewModel by viewModels<ScheduleLessonViewModel>()
    private var subject : SubjectEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScheduleLessonManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAppBar()
        setViewType()
        textListener()
        colorPickerListener()
        setButtonListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.setNavigationOnClickListener { finish() }
    }

    private fun setButtonListener(){
        binding.manageSubjectAddBtn.setOnClickListener {
            if(checkInputValid()){
                scheduleLessonViewModel.insertLesson()
                setResult(RESULT_OK)
                finish()
            }
        }
        binding.manageSubjectSaveBtn.setOnClickListener {
           if(checkInputValid()){
               scheduleLessonViewModel.updateLesson(
                   SubjectEntity(
                       subject?.id,
                       scheduleLessonViewModel.subjectName!!,
                       scheduleLessonViewModel.subjectLecturer!!,
                       scheduleLessonViewModel.subjectForegroundColor!!,
                       scheduleLessonViewModel.subjectBackgroundColor!!
                   )
               )
               setResult(RESULT_OK)
               finish()
           }
        }
        binding.manageSubjectDeleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Do You Want To Delete This Lesson ?")
                .setCancelable(false)
                .setPositiveButton("Yes") {dialogue, id ->
                    scheduleLessonViewModel.deleteLesson(subject!!)
                    setResult(RESULT_OK)
                    finish()
                }
                .setNegativeButton("No") {dialogue, id ->
                    dialogue.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun setViewType(){
        val intentType = intent.getStringExtra(EXTRA_TYPE)
        if(intentType?.equals(ActivityCodes.ACTIVITY_ADD) == true){
            binding.appBarLayout.topAppBar.title = "Add lesson"
            binding.manageSubjectDeleteBtn.visibility = View.GONE
            binding.manageSubjectSaveBtn.visibility = View.GONE
            setDataFromViewModel()
        }

        if(intentType?.equals(ActivityCodes.ACTIVITY_EDIT) == true){
            binding.appBarLayout.topAppBar.title = "Manage lesson"
            binding.manageSubjectAddBtn.visibility = View.GONE
            val subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0)
            getDataFromViewModel(subjectId)
        }
    }

    private fun colorPickerListener(){
        binding.manageBgColorPicker.setOnClickListener { colorPickerDialogue(binding.manageBgColorDisplay) }
        binding.manageFgColorPicker.setOnClickListener { colorPickerDialogue(binding.manageFgColorDisplay) }
    }

    private fun textListener(){
        binding.manageSubjectTxt.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                scheduleLessonViewModel.subjectName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.manageLecturerTxt.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                scheduleLessonViewModel.subjectLecturer = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun colorPickerDialogue(view: MaterialCardView){
        ColorPickerDialog.Builder(this)
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Confirm",
                ColorEnvelopeListener { envelope, fromUser ->
                    val hexColor = "#${envelope.hexCode}"
                    view.setCardBackgroundColor(Color.parseColor(hexColor))
                    scheduleLessonViewModel.subjectBackgroundColor = String.format("#%06X", (0xFFFFFF and binding.manageBgColorDisplay.cardBackgroundColor.defaultColor))
                    scheduleLessonViewModel.subjectForegroundColor = String.format("#%06X", (0xFFFFFF and binding.manageFgColorDisplay.cardBackgroundColor.defaultColor))
                })
            .setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            .show()

    }

    private fun setDataFromViewModel(){
        binding.manageSubjectTxt.editText?.setText(scheduleLessonViewModel.subjectName)
        binding.manageLecturerTxt.editText?.setText(scheduleLessonViewModel.subjectLecturer)
        binding.manageBgColorDisplay.setCardBackgroundColor(Color.parseColor(scheduleLessonViewModel.subjectBackgroundColor))
        binding.manageFgColorDisplay.setCardBackgroundColor(Color.parseColor(scheduleLessonViewModel.subjectForegroundColor))
    }

    private fun getDataFromViewModel(id: Long){
        scheduleLessonViewModel.getSpecificLesson(id).observe(this, {
            subject = it
            binding.manageSubjectTxt.editText?.setText(subject?.subjectName)
            binding.manageLecturerTxt.editText?.setText(subject?.subjectLecturer)
            if(subject?.subjectBackgroundColor?.isNotEmpty() == true){
                binding.manageBgColorDisplay.setCardBackgroundColor(Color.parseColor(subject?.subjectBackgroundColor))
                binding.manageFgColorDisplay.setCardBackgroundColor(Color.parseColor(subject?.subjectForegroundColor))
            }

            scheduleLessonViewModel.subjectLecturer = it?.subjectLecturer
            scheduleLessonViewModel.subjectName = it?.subjectName
            scheduleLessonViewModel.subjectBackgroundColor = it?.subjectBackgroundColor
            scheduleLessonViewModel.subjectForegroundColor = it?.subjectForegroundColor
        })
    }

    private fun checkInputValid() : Boolean {
        val subjectText = binding.manageSubjectTxt.editText
        val lecturerText = binding.manageLecturerTxt.editText
        if(subjectText?.text.isNullOrEmpty()){
            subjectText?.error = "Subject Name is required"
            return false
        }

        if(lecturerText?.text.isNullOrEmpty()){
            lecturerText?.error = "Subject Lecturer is required"
            return false
        }
        return true
    }
}