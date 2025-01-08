package com.simple.meditrack.ui.alarm_add.times

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.simple.meditrack.Deeplink
import com.simple.meditrack.EventName
import com.simple.meditrack.Param
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.sendEvent

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = arguments?.getInt(Param.HOUR) ?: c.get(Calendar.HOUR_OF_DAY)
        val minute = arguments?.getInt(Param.MINUTE) ?: c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        // Do something with the time the user picks.
        sendEvent(EventName.TIME, bundleOf(Param.HOUR to hourOfDay, Param.MINUTE to minute))
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class PickTimeDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.PICK_TIME
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is MainActivity) return false

        val fragment = TimePickerFragment()
        fragment.arguments = extras
        fragment.show(activity.supportFragmentManager, "")

        return true
    }
}