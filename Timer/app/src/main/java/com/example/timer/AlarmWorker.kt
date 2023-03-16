package com.example.timer

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Build
import android.security.identity.PersonalizationData
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class AlarmWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    companion object {
        const val CHANNEL_ID = "알람2"
    }

//    private val soundPool = SoundPool.Builder().build()
//    private var soundAlarm:Int? = null

    override fun doWork(): Result {
//        initSoundPool()
//        playAlarm()


        Intent(applicationContext, AlarmService::class.java).let {
            it.action = Action.PLAY
            startForegroundService(applicationContext, it)
        }

        createNotificationChannel(applicationContext)
        createNotification(applicationContext)

        return Result.success()
    }
//
//    private fun initSoundPool(){
//
//        soundAlarm = soundPool.load(applicationContext,R.raw.end,1)
//    }
//
//    private fun playAlarm(){
//        soundAlarm?.let {
//            soundPool.play(it,1F,1F,0,1,1F)
//        }
//    }
//
//    private fun stopAlarm(){
//        soundAlarm?.let {
//            soundPool.stop(it)
//        }
//    }


    private fun createNotificationChannel(context: Context?) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "알람"
            val descriptionText = "Alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context) {

        val pendingIntent = Intent(context, MainActivity::class.java).let {
            PendingIntent.getActivity(context, 0, it, 0)
        }



        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_1_)
            .setContentTitle("알람이 시작됨")
            .setContentText("알람이 진행 중입니다. 10분마다 알람이 반복됩니다.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)



        context?.let {
            with(NotificationManagerCompat.from(it)) {
                if (ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

                ) {
                    return
                }


                builder?.let {
                    notify(1011, it.build())
                }
            }
        }

    }


}