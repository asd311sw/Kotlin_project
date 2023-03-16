package com.example.timer.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.timer.R
import com.example.timer.databinding.FragmentDialogBinding
import com.example.timer.databinding.FragmentTimerBinding
import com.example.timer.databinding.ItemListBinding
import java.util.*
import kotlin.concurrent.timer

class TimerFragment : Fragment() {
    private var hour:Int = 0
    private var minute:Int = 0
    private var second:Int = 0
    private var timerTask:Timer? = null
    private val soundPool = SoundPool.Builder().build()
    private var soundTick:Int = 0
    private var soundEnd:Int = 0
    private var isPlaying:Boolean = false


    private val binding: FragmentTimerBinding by lazy {
        FragmentTimerBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initSoundPool()


        binding.settingButton.setOnClickListener {
            alertDialog()

        }



        binding.pauseButton.setOnClickListener {
            stopRunMusic()
            timerTask?.cancel()
        }
        binding.playButton.setOnClickListener {

            if(isPlaying)
                return@setOnClickListener

            isPlaying = true

            playRunMusic()

            timerTask = timer(period = 1000) {

                activity?.runOnUiThread {
                    playTimer()
                    binding.seekBar.progress -= 1000
                }

            }
        }



        binding.pauseButton.setOnClickListener {

            if(!isPlaying)
                return@setOnClickListener

            isPlaying = false

            stopRunMusic()
            timerTask?.cancel()
        }


        binding.initButton.setOnClickListener {
            if(!isPlaying)
                return@setOnClickListener

            isPlaying = false

            stopRunMusic()
            timerTask?.cancel()

            hour = 0
            minute = 0
            second = 0


            initTextView()
        }


        return binding.root
    }



    fun initSoundPool(){

        soundTick = soundPool.load(context, R.raw.running_clock, 1)
        soundEnd = soundPool.load(context, R.raw.end, 1)

    }

    fun playRunMusic(){
        soundTick.let {
            soundPool.play(it,1F, 1F, 0, 1, 1F)
        }
    }

    fun playEndMusic(){

        soundEnd.let {
            soundPool.play(it,1F,1F,0,1,1F)
        }

        AlertDialog.Builder(context)
            .setTitle("알림")
            .setIcon(R.drawable.timer)
            .setMessage("설정된 시간이 모두 경과함.")
            .setPositiveButton("확인",DialogInterface.OnClickListener { dialogInterface, i ->
                soundEnd.let {
                    soundPool.stop(it)
                }

            })
            .setCancelable(false)
            .create()
            .show()

    }


    fun stopRunMusic(){

        soundTick.let {
            soundPool.stop(it)
        }


    }

    fun initTextView(){
        binding.apply {
            hourTextView.text = hour.toString()
            minuteTextView.let {
                var m = minute.toString()
                if(m.toInt() < 10)
                    m = "0" + m
                it.text = m
            }
            secondTextView.let {
                var s = second.toString()
                if(s.toInt() < 10)
                    s = "0" + s
                it.text = s
            }
        }


    }

    fun playTimer(){
        if(hour == 0 && minute == 0 && second == 0) {
            playEndMusic()
            stopRunMusic()
            timerTask?.cancel()
        }
        else{
            when(hour){
                0 -> {
                    if(minute == 0)
                        second-=1
                    else{
                        if(second == 0) {
                            minute -= 1
                            second = 59
                        }
                        else
                            second -= 1
                    }
                }
                else -> {
                    if(minute == 0 && second == 0) {
                        hour -= 1
                        minute = 59
                        second = 59
                    }
                    else{
                        if(minute == 0)
                            second-=1
                        else{
                            if(second == 0) {
                                minute -= 1
                                second = 59
                            }
                            else
                                second -= 1
                        }
                    }

                }

            }

        }



        initTextView()
    }


    fun alertDialog(){
        val dialogBinding = FragmentDialogBinding.inflate(layoutInflater)

        dialogBinding.apply {
            hourNumberPicker.minValue = 0
            hourNumberPicker.maxValue = 59
            minuteNumberPicker.minValue = 0
            minuteNumberPicker.maxValue = 59
            secondNumberPicker.minValue = 0
            secondNumberPicker.maxValue = 59
        }


        AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setIcon(R.drawable.timer)
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                binding.apply {

                    hour = dialogBinding.hourNumberPicker.value
                    minute = dialogBinding.minuteNumberPicker.value
                    second = dialogBinding.secondNumberPicker.value


                    val ms = (3600 * hour + 60 * minute + second) * 1000

                    binding.seekBar.max = ms
                    binding.seekBar.progress = ms

                    hourTextView.text = hour.toString()
                    minuteTextView.let {
                        var minute = minute.toString()

                        if(minute.toInt() < 10)
                            minute = "0" + minute
                        it.text = minute
                    }
                    secondTextView.let {
                        var second = second.toString()
                        if(second.toInt() < 10)
                            second = "0" + second
                        it.text = second
                    }

                }
            })
            .setNegativeButton("취소", null)
            .create()
            .show()
    }




}