package com.simple.meditrack.ui.base

import android.os.Bundle
import android.view.View
import com.simple.ai.english.ui.base.transition.TransitionFragment
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.Deeplink
import com.simple.meditrack.databinding.FragmentPageBinding
import com.simple.meditrack.utils.sendDeeplink

class PageFragment(val index: String) : TransitionFragment<FragmentPageBinding, PageViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (index == "1") {
            sendDeeplink(Deeplink.ALARM_LIST)
        }
    }

}

class PageViewModel : TransitionViewModel()