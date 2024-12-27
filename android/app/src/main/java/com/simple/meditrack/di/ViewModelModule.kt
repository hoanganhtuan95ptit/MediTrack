package com.simple.meditrack.di

import com.simple.ai.english.ui.base.transition.TransitionGlobalViewModel
import com.simple.meditrack.ui.notification.NotificationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val viewModelModule = module {

    viewModel {
        NotificationViewModel()
    }

    viewModel {
        TransitionGlobalViewModel()
    }
}