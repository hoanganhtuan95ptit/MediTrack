package com.simple.meditrack.di

import com.simple.meditrack.data.repositories.AlarmRepositoryImpl
import com.simple.meditrack.data.repositories.AppRepositoryImpl
import com.simple.meditrack.data.repositories.MedicineRepositoryImpl
import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.AppRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import org.koin.dsl.module

@JvmField
val repositoryModule = module {

    single<AppRepository> {
        AppRepositoryImpl(get(), get())
    }

    single<AlarmRepository> {
        AlarmRepositoryImpl(get())
    }

    single<MedicineRepository> {
        MedicineRepositoryImpl(get())
    }
}