package com.polibatam.synchedule.ui.schedule.lesson

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polibatam.synchedule.data.entity.SubjectEntity
import com.polibatam.synchedule.databinding.ItemScheduleLessonBinding


class ScheduleLessonRecyclerViewAdapter : RecyclerView.Adapter<ScheduleLessonRecyclerViewAdapter.ScheduleLessonViewHolder>() {

    private var _lessons = ArrayList<SubjectEntity>()
    private var onItemLessonClickCallback : OnItemLessonClickCallback? = null

    fun setLessonData(lesson: List<SubjectEntity>){
        _lessons.clear()
        _lessons.addAll(lesson)
    }

    fun setOnItemLessonClickCallback(onItemLessonClickCallback: OnItemLessonClickCallback){
        this.onItemLessonClickCallback = onItemLessonClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleLessonViewHolder {
        val binding = ItemScheduleLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleLessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleLessonViewHolder, position: Int) {
        holder.bindData(_lessons[position])
    }

    override fun getItemCount(): Int {
        return _lessons.size
    }

    inner class ScheduleLessonViewHolder(val binding : ItemScheduleLessonBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(lesson: SubjectEntity){
            with(binding){
                lessonName.text = lesson.subjectName
                lessonLecturer.text = lesson.subjectLecturer
                itemView.setOnClickListener{ onItemLessonClickCallback?.onItemLessonClicked(lesson) }
            }
        }
    }

    interface OnItemLessonClickCallback {
        fun onItemLessonClicked(lesson: SubjectEntity)
    }
}