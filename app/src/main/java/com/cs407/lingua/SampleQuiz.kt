package com.cs407.lingua

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SampleQuiz (val quizzes: ArrayList<String>) : RecyclerView.Adapter<SampleQuiz.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.defaultText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleQuiz.ViewHolder, position: Int) {
        holder.firstName.text = quizzes[position]
    }

    override fun getItemCount(): Int {
        return quizzes.size
    }
}