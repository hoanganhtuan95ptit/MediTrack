package com.simple.meditrack.ui.notification

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.core.os.bundleOf
import com.simple.coreapp.ui.base.activities.BaseViewBindingActivity
import com.simple.meditrack.Deeplink
import com.simple.meditrack.Param
import com.simple.meditrack.databinding.ActivityMainBinding
import com.simple.meditrack.databinding.ActivityNotificationBinding
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

        sendDeeplink(Deeplink.NOTIFICATION, extras = bundleOf(Param.TRANSITION_DURATION to 0L))
    }
}