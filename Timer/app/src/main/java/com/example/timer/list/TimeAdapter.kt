package com.example.timer.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.timer.databinding.ItemListBinding

class TimeAdapter:RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    var timeList:ArrayList<Time> = ArrayList()

    inner class TimeViewHolder(val binding:ItemListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(time: Time){
            binding.dateTextView.text = time.date
            binding.timeTextView.text = "${time.minute}분 ${time.second}초"

            binding.cancelButton.setOnClickListener {
                timeList.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TimeViewHolder(view)
    }

    override fun getItemCount(): Int = timeList.size

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(timeList[position])
    }



}