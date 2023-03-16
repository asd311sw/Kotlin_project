package com.example.timer.list

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.timer.AlarmReceiver
import com.example.timer.AlarmWorker
import com.example.timer.MainActivity
import com.example.timer.R
import com.example.timer.databinding.ItemListBinding
import com.example.timer.fragment.AlarmFragment

class AlarmAdapter:RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
    var alarmList:ArrayList<Alarm> = ArrayList()

    companion object{
        const val CHANNEL_ID = "alarm1"
    }

    private var alarmManager:AlarmManager? = null
    private var alarmIntent:PendingIntent? = null


    inner class AlarmViewHolder(val binding:ItemListBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(alarm: Alarm){

            binding.dateTextView.text = alarm.date
            binding.timeTextView.text = "${alarm.hour}시 ${alarm.minute}분"
            binding.labelTextView.text = "알람 시간"

            binding.cancelButton.setOnClickListener {
                alarmList.removeAt(adapterPosition)
                notifyDataSetChanged()

                alarmManager?.cancel(alarmIntent)
                createNotificationChannel(binding.root.context)
                createNotification(binding.root.context)
            }


        }

    }

    fun initAlarm(alarmManager: AlarmManager?,alarmIntent: PendingIntent?){
        this.alarmManager = alarmManager
        this.alarmIntent = alarmIntent
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AlarmViewHolder(view)



    }

    override fun getItemCount(): Int = alarmList.size

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {

        holder.bind(alarmList[position])
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

    private fun createNotification(context: Context?){
        var builder = context?.let {

            val pendingIntent = Intent(it,MainActivity::class.java).let {
                PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_ONE_SHOT)
            }



            NotificationCompat.Builder(it, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm_1_)
                .setContentTitle("알람이 삭제됨")
                .setContentText("알람이 삭제되었습니다.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        }

        context?.let {
            with(NotificationManagerCompat.from(it)){
                if (ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

                ) {
                    return
                }


                builder?.let {
                    notify(1011,it.build())
                }
            }
        }

    }




}