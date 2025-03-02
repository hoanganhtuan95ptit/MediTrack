package com.simple.meditrack.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.simple.meditrack.Param
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.AlarmReceiver
import java.util.Calendar

object AlarmUtils {

    fun setDailyAlarm(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        // Tính thời gian 0h hàng ngày
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

//             Nếu đã qua 0h hôm nay, đặt cho ngày mai
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Đặt báo thức chính xác
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun setAlarm(context: Context, alarm: Alarm) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent và PendingIntent
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Param.ID, alarm.id)
        intent.putExtra(Param.ID_INT, alarm.idInt)
        val pendingIntent = PendingIntent.getBroadcast(context, alarm.idInt, intent, PendingIntent.FLAG_IMMUTABLE)

        // Thiết lập thời gian
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour)
        calendar.set(Calendar.MINUTE, alarm.minute)
        calendar.set(Calendar.SECOND, 0)

        if (System.currentTimeMillis() > calendar.timeInMillis) {
            return
        }

        // Đặt báo thức chính xác
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context, idInt: Intent?) {

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, idInt?.extras?.getInt(Param.ID_INT) ?: 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // hủy báo thức
        alarmManager.cancel(pendingIntent)
    }
}