package com.example.timer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timer.R
import com.example.timer.databinding.FragmentStopwatchBinding
import com.example.timer.databinding.FragmentTimerBinding
import com.example.timer.list.Time
import com.example.timer.list.TimeAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class StopwatchFragment: Fragment() {
    private var minute:Int = 0
    private var second:Int = 0
    private var isPlaying:Boolean = false
    private val adapter:TimeAdapter by lazy {
        TimeAdapter()
    }

    private val timeList:ArrayList<Time> = ArrayList()
    private lateinit var timerTask: Timer

    private val binding: FragmentStopwatchBinding by lazy {
        FragmentStopwatchBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initView()

        binding.startFab.setOnClickListener {
            if(!isPlaying) {
                playStopwatch()
            }
            else{
                pauseStopwatch()
            }
        }


        binding.restartFab.setOnClickListener {
            initStopwatch()
        }


        binding.labButton.setOnClickListener {

            val format = SimpleDateFormat("yyyy.MM.dd hh:mm")
            val date = format.format(Date())
            timeList.add(Time(date,minute,second))
            adapter.timeList = timeList
            adapter.notifyDataSetChanged()

        }

        return binding.root
    }


    private fun playStopwatch(){
        binding.startFab.setBackgroundResource(R.drawable.ic_pause_24)

        timerTask = timer(period = 1000){

            activity?.runOnUiThread {
                second += 1

                if(second == 60){
                    binding.minuteTextView.text = (++minute).toString()
                    second = 0
                    binding.secondTextView.text = "00"

                }
                else if(second >= 10){
                    binding.secondTextView.text = second.toString()
                }
                else{
                    binding.secondTextView.text = "0" + second
                }


            }
        }


        isPlaying = true


    }

    private fun pauseStopwatch(){
        binding.startFab.setBackgroundResource(R.drawable.ic_play_arrow)
        timerTask.cancel()
        isPlaying = false

    }

    private fun initStopwatch(){

        timerTask.cancel()
        binding.minuteTextView.text = "0"
        binding.secondTextView.text = "00"
        minute = 0
        second = 0

    }

    private fun initView(){
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }



}