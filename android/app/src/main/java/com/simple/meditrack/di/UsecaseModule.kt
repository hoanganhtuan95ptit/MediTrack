package com.simple.meditrack.di

import com.simple.meditrack.domain.usecases.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.GetListNotificationAsyncUseCase
import org.koin.dsl.module

@JvmField
val usecaseModule = module {

    single {
        GetAlarmByIdAsyncUseCase(get())
    }

    single {
        GetListNotificationAsyncUseCase(get())
    }
}