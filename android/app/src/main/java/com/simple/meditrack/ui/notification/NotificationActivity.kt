@file:Suppress("DEPRECATION")

package com.simple.meditrack.ui.notification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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

        super.onCreate(savedInstanceState)

        val extras = bundleOf(Param.TRANSITION_DURATION to 0L)
        extras.putAll(intent.extras ?: bundleOf())

        sendDeeplink(Deeplink.NOTIFICATION_VIEW, extras = extras)


        // Hiển thị Activity trên màn hình khóa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {

            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {

            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }

        setupTheme(this)
        setupNavigation(this)

        setupBackPress()
    }

    private fun setupBackPress() = onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
        }
    })
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