package com.simple.meditrack.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simple.meditrack.ui.notification.NotificationActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationIntent = Intent(context, NotificationActivity::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        notificationIntent.putExtras(notificationIntent)
        context!!.startActivity(notificationIntent)
    }
}