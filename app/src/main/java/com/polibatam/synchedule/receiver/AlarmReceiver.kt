package com.polibatam.synchedule.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.polibatam.synchedule.R
import com.polibatam.synchedule.ui.schedule.detail.ScheduleDetailActivity
import com.polibatam.synchedule.utils.AlarmCodes
import dagger.hilt.android.AndroidEntryPoint

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val scheduleId = intent.getIntExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_SCHEDULE_ID, 0)

        if(scheduleId != 0){
            val scheduleTitle = intent.getStringExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_TITLE)
            val startTime = intent.getStringExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_START_TIME)
            val endTime = intent.getStringExtra(AlarmCodes.EXTRA_SCHEDULE_ALARM_END_TIME)
            val contentNotification = "$scheduleTitle at $startTime - $endTime"
            Log.d("AlarmReceiver", "Alarm Fired : $startTime - $endTime")
            createNotification(context, AlarmCodes.SCHEDULE_ALARM_ID, "Upcoming Schedule", contentNotification, scheduleId)
        }


    }

    private fun createNotification(context: Context, channelId: String, contentTitle : String, contentText: String, notificationId : Int){
        val intent = Intent(context, ScheduleDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_synchedule_logo)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(AlarmCodes.SCHEDULE_ALARM_ID, contentTitle, importance).apply {
                description = contentText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}