package com.simple.meditrack.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.simple.meditrack.App
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.notification.NotificationActivity


object NotificationUtils {

    fun sendNotification(context: Context, alarm: Alarm) {

        val intent = Intent(context, NotificationActivity::class.java)
        intent.putExtra(Param.ID, alarm.id)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)

        val pendingIntent = PendingIntent.getActivity(context, alarm.idInt, intent, PendingIntent.FLAG_MUTABLE)


        var soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (soundUri == null) {
            // Nếu không có nhạc báo thức, dùng nhạc chuông mặc định
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }

        val channelId = "alarm_notification_2"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId, "Alarm Notification", NotificationManager.IMPORTANCE_HIGH)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.img_notification)
            .setContentTitle(alarm.name)
            .setContentText(alarm.note)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alarm.idInt, notification)
    }

    fun cancelNotification(id: Int) {

        val notificationManager = App.shared.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id)
    }
}