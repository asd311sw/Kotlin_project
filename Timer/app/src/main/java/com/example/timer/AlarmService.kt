package com.example.timer

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmService: Service() {

    companion object{
        const val CHANNEL_ID = "service_alarm"
        const val NOTIFICATION_ID = 1012
    }

    private val soundPool = SoundPool.Builder().build()
    private var soundAlarmId:Int? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                Action.PLAY -> {
                    playAlarm()
                    initSoundPool()
                    startForegroundService()
                }

                Action.STOP -> {
                    stopAlarm()
                    stopForegroundService()
                }

            }

        }



        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    private fun initSoundPool(){


        soundAlarmId = soundPool.load(applicationContext,R.raw.miley,1)

        val sharedPreferences = getSharedPreferences("get",Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putInt("SoundID", soundAlarmId!!)
            apply()
        }

    }

    private fun playAlarm(){
        soundPool.setOnLoadCompleteListener { soundPool, i, i2 ->

            soundPool.play(i,1F,1F,0,-1,1F)

        }

    }



    private fun stopAlarm() {

        val sharedPreferences = getSharedPreferences("get",Context.MODE_PRIVATE)

        soundAlarmId = sharedPreferences.getInt("SoundID",0)

        soundAlarmId?.let {
            soundPool.stop(it)
        }

    }



    private fun startForegroundService(){
        createNotificationChannel(applicationContext)
        createNotification(applicationContext)?.let {
            startForeground(NOTIFICATION_ID,it)
        }

    }

    private fun stopForegroundService(){

        stopForeground(true)
        stopSelf()

        with(NotificationManagerCompat.from(applicationContext)){
            cancel(NOTIFICATION_ID)
        }

    }


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

    private fun createNotification(context: Context):Notification? {

        val pendingIntent = Intent(context,MainActivity::class.java).let {
            PendingIntent.getActivity(context, 0, it, 0)
        }

        val alarmIntent = Intent(context,AlarmService::class.java).let {
            it.action = Action.STOP
            PendingIntent.getService(context,0,it,0)
        }

        val builder =  NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_1_)
            .setContentTitle("알람음 재생 중")
            .setContentText("알람음이 재생 중입니다.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.alarm_1_,"Stop",alarmIntent)


        with(NotificationManagerCompat.from(context)){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

            ) {
                return@with
            }


            return builder.build()

        }

        return null
    }







}