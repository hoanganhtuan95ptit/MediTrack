package com.simple.meditrack.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.simple.meditrack.Param
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.ui.notification.NotificationActivity
import com.simple.meditrack.utils.AlarmUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val getListAlarmAsyncUseCase: GetListAlarmAsyncUseCase by inject()

    private val getAlarmByIdAsyncUseCase: GetAlarmByIdAsyncUseCase by inject()

    override fun onReceive(context: Context?, intent: Intent?) {

        context ?: return

        val id = intent?.extras?.getString(Param.ID)

        if (!id.isNullOrBlank()) GlobalScope.launch(Dispatchers.IO) {

            val alarm = getAlarmByIdAsyncUseCase.execute(GetAlarmByIdAsyncUseCase.Param(id)).first()

            if (alarm == null) {

                AlarmUtils.cancelAlarm(context = context, intent)
                return@launch
            }

            showNotification(context, "AlarmReceiverNotification", SimpleDateFormat("hh:mm:ss").format(System.currentTimeMillis()))

            val notificationIntent = Intent(context, NotificationActivity::class.java)
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            notificationIntent.putExtras(intent.extras ?: bundleOf())
            context.startActivity(notificationIntent)
        } else GlobalScope.launch(Dispatchers.IO) {

            showNotification(context, "AlarmReceiver", SimpleDateFormat("hh:mm:ss").format(System.currentTimeMillis()))

            val list = getListAlarmAsyncUseCase.execute().first()

            list.forEach {

                AlarmUtils.setAlarm(context, it)
            }

            AlarmUtils.setDailyAlarm(context)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}