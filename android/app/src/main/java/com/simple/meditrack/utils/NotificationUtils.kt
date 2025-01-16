package com.simple.meditrack.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.simple.meditrack.App
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.notification.NotificationActivity


object NotificationUtils {

    fun sendNotification(context: Context, alarm: Alarm) {

        val intent = Intent(context, NotificationActivity::class.java)
        intent.putExtra(Param.ID, alarm.id)
        intent.putExtra(Param.FROM_NOTIFICATION, true)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)

        val pendingIntent = PendingIntent.getActivity(context, alarm.idInt, intent, PendingIntent.FLAG_MUTABLE)

        val channelId = "alarm_notification"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId, "Alarm Notification", NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.img_notification)
            .setContentTitle(alarm.name)
            .setContentText(alarm.note)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(alarm.idInt, notification)
    }


    fun sendNotification(
        context: Context,

        intent: Intent? = null,

        requestCode: Int = 0,

        channelId: String? = null,
        channelName: String? = null,

        title: String,
        message: String = "",

        notificationId: Int = 0
    ) {

        val intentWrap = intent ?: Intent(context, MainActivity::class.java)
        intentWrap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(context, requestCode, intentWrap, PendingIntent.FLAG_MUTABLE)

        val channelIdWrap = channelId ?: "default_notification"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelIdWrap, channelName ?: "Default Notification", NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelIdWrap)
            .setSmallIcon(R.drawable.img_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(id: Int) {

        val notificationManager = App.shared.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id)
    }
}