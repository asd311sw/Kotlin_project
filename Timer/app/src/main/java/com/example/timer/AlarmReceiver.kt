package com.example.timer

import android.Manifest
import android.app.*
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.timer.fragment.AlarmFragment
import com.example.timer.list.Alarm
import com.example.timer.list.AlarmAdapter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private var alarmHour: Int = 0
    private var alarmMinute: Int = 0

    private val alarmRequest =
        PeriodicWorkRequestBuilder<AlarmWorker>(5, TimeUnit.MINUTES).build()

    override fun onReceive(p0: Context?, p1: Intent?) {

        p1?.let {
            alarmHour = it.getIntExtra("hour", 0)
            alarmMinute = it.getIntExtra("minute", 0)
        }


        playAlarm(p0)
        repeatAlarm(p0)
    }


    private fun playAlarm(context: Context?) {
        context?.let {
            WorkManager.getInstance(it).enqueue(alarmRequest)
        }

    }


    private fun repeatAlarm(context: Context?) {

        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let {
            it.putExtra("hour", alarmHour)
            it.putExtra("minute", alarmMinute)
            it.flags = Intent.FLAG_RECEIVER_FOREGROUND
            PendingIntent.getBroadcast(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmHour)
            set(Calendar.MINUTE, alarmMinute)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 60000 * 10,
            alarmIntent
        )




    }


}