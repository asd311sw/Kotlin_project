package com.example.timer.fragment

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timer.AlarmReceiver
import com.example.timer.AlarmService
import com.example.timer.MainActivity
import com.example.timer.R
import com.example.timer.databinding.FragmentAlarmBinding
import com.example.timer.databinding.FragmentStopwatchBinding
import com.example.timer.list.Alarm
import com.example.timer.list.AlarmAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmFragment: Fragment() {

    companion object {
        const val CHANNEL_ID = "channel1"
    }

    private var alarmManager:AlarmManager? = null
    private var alarmIntent:PendingIntent? = null

    private val alarmList:ArrayList<Alarm> = ArrayList()
    private val alarmAdapter = AlarmAdapter()


    private val binding: FragmentAlarmBinding by lazy {
        FragmentAlarmBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initView()
        createNotificationChannel()


        binding.checkButton.setOnClickListener {

            val format = SimpleDateFormat("yyyy.MM.dd hh:mm")
            val date = format.format(Date())
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute

            alarmList.add(Alarm(date, hour, minute))

            alarmAdapter.alarmList = alarmList
            alarmAdapter.notifyDataSetChanged()
            createNotification()
            createAlarm()
        }

        return binding.root
    }

    private fun initView(){


        binding.recyclerView.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(context)
        }


    }

    override fun onDestroy() {
        super.onDestroy()


        alarmIntent?.let { alarmAdapter.initAlarm(alarmManager!!, it) }

    }



    private fun createAlarm(){

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context,AlarmReceiver::class.java).let {
            it.putExtra("hour",binding.timePicker.hour)
            it.putExtra("minute",binding.timePicker.minute)
            it.flags = Intent.FLAG_RECEIVER_FOREGROUND
            PendingIntent.getBroadcast(context,0,it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmAdapter.initAlarm(alarmManager,alarmIntent)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,binding.timePicker.hour)
            set(Calendar.MINUTE,binding.timePicker.minute)
        }

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )


    }






    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Notification"
            val descriptionText = "Alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(){

        val pendingIntent = Intent(context,MainActivity::class.java)?.let {
            PendingIntent.getActivity(context,0,it,0)
        }


        var builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_1_)
            .setContentTitle("Alarm Notification")
            .setContentText("${binding.timePicker.hour}시 ${binding.timePicker.minute}분에 알람 예정됨.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        with(NotificationManagerCompat.from(requireContext())){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

            ) {
                return
            }
            notify(1011,builder.build())
        }
    }
}