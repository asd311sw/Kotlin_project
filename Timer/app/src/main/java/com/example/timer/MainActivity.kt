package com.example.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.timer.databinding.ActivityMainBinding
import com.example.timer.fragment.AlarmFragment
import com.example.timer.fragment.StopwatchFragment
import com.example.timer.fragment.TimerFragment

class MainActivity : AppCompatActivity() {

    companion object {
        var minute:Int = 0
        var second:Int = 0
    }

    private val stopwatchFragment:Fragment by lazy {
        StopwatchFragment()
    }

    private val timerFragment:TimerFragment = TimerFragment()

    private val alarmFragment:Fragment by lazy {
        AlarmFragment()
    }

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        changeFragment(timerFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId) {
                R.id.timerFragment -> changeFragment(timerFragment)

                R.id.alarmFragment -> changeFragment(alarmFragment)

                R.id.stopwatchFragment -> changeFragment(stopwatchFragment)
            }



            true
        }

    }


    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout,fragment)
            .commit()


    }






}