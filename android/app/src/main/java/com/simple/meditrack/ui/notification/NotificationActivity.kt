package com.simple.meditrack.ui.notification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import com.simple.coreapp.ui.base.activities.BaseViewBindingActivity
import com.simple.meditrack.Deeplink
import com.simple.meditrack.Param
import com.simple.meditrack.databinding.ActivityNotificationBinding
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.NavigationView
import com.simple.meditrack.utils.NavigationViewImpl
import com.simple.meditrack.utils.sendDeeplink
import com.simple.meditrack.utils.setupTheme

class NotificationActivity : BaseViewBindingActivity<ActivityNotificationBinding>(),
    NavigationView by NavigationViewImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        setupTheme(this)
        setupNavigation(this)

        val extras = bundleOf(Param.TRANSITION_DURATION to 0L)
        extras.putAll(intent.extras ?: bundleOf())

        sendDeeplink(Deeplink.NOTIFICATION_VIEW, extras = extras)
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class NotificationDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.NOTIFICATION
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity is NotificationActivity) return false

        val intent = Intent(activity, NotificationActivity::class.java)
        intent.putExtras(extras ?: bundleOf())

        activity.startActivity(intent)

        return true
    }
}