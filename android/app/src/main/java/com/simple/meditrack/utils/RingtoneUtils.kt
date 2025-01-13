package com.simple.meditrack.utils

import android.media.Ringtone
import android.media.RingtoneManager
import com.simple.meditrack.App


object RingtoneUtils {

    private var ringtone: Ringtone? = null

    init {

        // Lấy URI của âm thanh TYPE_ALARM
        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            // Nếu không có âm thanh báo thức, dùng TYPE_NOTIFICATION
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        // Tạo Ringtone từ URI
        ringtone = RingtoneManager.getRingtone(App.shared, alarmUri)
    }

    fun play() {

        ringtone?.play()
    }

    fun stop() {

        ringtone?.stop()
    }
}