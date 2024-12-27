package com.simple.meditrack.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simple.meditrack.R
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


        // Intent và PendingIntent
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        // Thiết lập thời gian
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis() + 30 * 1000)
//        calendar.set(Calendar.HOUR_OF_DAY, 15) // Hẹn giờ lúc 15:00 sáng
//        calendar.set(Calendar.MINUTE, 54)
//        calendar.set(Calendar.SECOND, 0)


        // Đặt lịch lặp lại hàng ngày
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,  // Thời gian lặp lại mỗi ngày
            pendingIntent
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            pendingIntent
        )
    }
}